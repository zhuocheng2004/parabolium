
#include <chrono>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <thread>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <verilated.h>
#include <verilated_vcd_c.h>

#include "Vtop.h"

#include "defines.h"
#include "sim_error.h"
#include "types.h"

static const unsigned int SIZE = 1 << 14;
static const unsigned int MASK = SIZE - 1;
static const unsigned int BASE = 0x80000000U;

static const u8 AXI5_OKAY   = 0x0;
static const u8 AXI5_SLVERR = 0x2; // unsupported read/write; failed action; device powered down
static const u8 AXI5_DECERR = 0x3; // illegal address

static inline u8 make_resp(u8 axi_resp, bool ok) {
    return (axi_resp << 1) | (ok ? 0x1 : 0x0);
}


// simulate peripheral behaviours (like delays)
static bool sim_perip   = false;

// RAM
static u8 *mem;

// statiscial && debug variables
static bool should_stop = false;
static bool stop_error  = false;

static unsigned int inst_cnt = 0;

// read/write variables
static bool reading = false;
static u32  raddr   = 0;

static bool writing = false;
static u32  waddr   = 0;
static u32  wdata   = 0;
static u8   wmask   = 0;

// simulate UART QUEUE behaviour
static const u32 UART_QUEUE_MAX = 0x16;
static u32 uart_clk_div = 0;
static u32 uart_queue_used = 0;
static u32 uart_counter = 0;

extern "C" void sim_read(u32 raddr) {
    if (reading) {
        std::cerr << "ERROR: Another reading is in progress." << std::endl;
        should_stop = true;
        stop_error = true;
    } else {
        ::raddr = raddr;
        reading = true;
    }
}

extern "C" u8 sim_read_ok(u32 *rdata_ptr) {
    if (!reading) {
        std::cerr << "ERROR: Reading not started." << std::endl;
        should_stop = true;
        stop_error = true;
        return 0;
    } else {
        u32 rdata;
        u8 resp = make_resp(AXI5_DECERR, true);

        if (raddr >= BASE && raddr < BASE + SIZE) {
            u32 addr = raddr & MASK & ~0x3u;
            rdata = mem[addr] + (mem[addr + 1] << 8) + (mem[addr + 2] << 16) + (mem[addr + 3] << 24);

            resp = make_resp(AXI5_OKAY, true);
        } else {
            std::cerr << "WARNING: LOAD @ " << std::hex << std::setw(8) << raddr << std::endl;
            rdata = 0;

            resp = make_resp(AXI5_DECERR, true);
        }

        // std::cout << "LOAD @ " << std::hex << std::setw(8) << raddr << ", rdata=" << rdata << std::endl;
        *rdata_ptr = rdata;

        if (resp & 0x1)
            reading = false;

        return resp;
    }
}

extern "C" void sim_write(u32 waddr, u32 wdata, u8 wmask) {
    // std::cout << "STORE @ " << std::hex << std::setw(8) << waddr << ", wdata=" << wdata << ", wmask=" << (int) wmask << std::endl;
    if (writing) {
        std::cerr << "ERROR: Another writing is in progress." << std::endl;
        should_stop = true;
        stop_error = true;
    } else {
        ::waddr = waddr;
        ::wdata = wdata;
        ::wmask = wmask;
        writing = true;
    }
}

extern "C" u8 sim_write_ok() {
    if (!writing) {
        std::cerr << "ERROR: Writing not started." << std::endl;
        should_stop = true;
        stop_error = true;
        return 0;
    } else {
        u8 resp = make_resp(AXI5_DECERR, true);
        // std::cout << "STORE @ " << std::hex << std::setw(8) << waddr << ", wdata=" << wdata << ", wmask=" << (int) wmask << std::endl;

        if (!(wmask & 0xf)) {
            resp = make_resp(AXI5_OKAY, true);
        } else if (waddr >= BASE && waddr < BASE + SIZE) {
            u32 addr = waddr & MASK & ~0x3u;
            if (wmask & 0x1)
                mem[addr] = wdata & 0xff;
            if (wmask & 0x2)
                mem[addr + 1] = (wdata >> 8) & 0xff;
            if (wmask & 0x4)
                mem[addr + 2] = (wdata >> 16) & 0xff;
            if (wmask & 0x8)
                mem[addr + 3] = (wdata >> 24) & 0xff;

            resp = make_resp(AXI5_OKAY, true);

        } else if (waddr == MISC + STOP) {
            if ((wmask & 0x1) && (wdata & 0xff))
                should_stop = true;

            resp = make_resp(AXI5_OKAY, true);

        } else if (waddr == MISC + LED) {
            if (wmask & 0x1)
                putchar(wdata & 0xff);
                fflush(stdout);
            
            resp = make_resp(AXI5_OKAY, true);

        } else if (waddr == UART + UART_CLK_DIV) {
            if (wmask & 0x3 == 0x3) {
                uart_clk_div = wdata & 0xffff;
            }

            resp = make_resp(AXI5_OKAY, true);
        } else if (waddr == UART + UART_TX) {
            if (sim_perip) {
                if (wmask & 0x1) {
                    if (uart_queue_used >= UART_QUEUE_MAX) {
                        resp = make_resp(AXI5_OKAY, false);
                    } else {
                        uart_queue_used++;
                        resp = make_resp(AXI5_OKAY, true);
                    }
                } else {
                    resp = make_resp(AXI5_OKAY, true);
                }
            } else {
                resp = make_resp(AXI5_OKAY, true);
            }
        } else {
            std::cerr << "WARNING: SAVE @ " << std::hex << std::setw(8) << waddr << ", data=" << wdata << ", mask=" << (u32)wmask << std::endl;

            resp = make_resp(AXI5_DECERR, true);
        }

        if (resp & 0x1)
            writing = false;

        return resp;
    }
}

extern "C" void sim_stop() {
    should_stop = true;
}

extern "C" void sim_error(u8 error_type, u32 info0) {
    std::cerr << "ERROR: ";
    switch (error_type) {
        case ERROR_NONE:
            std::cerr << "error reported on success?";
            break;
        case ERROR_IFU:
            std::cerr << "IFU: failed to fetch instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
            break;
        case ERROR_EXU_INVALID:
            std::cerr << "EXU: invalid instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
            break;
        case ERROR_MAU_LOAD:
            std::cerr << "MAU: load failed, instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
            break;
        case ERROR_MAU_STORE:
            std::cerr << "MAU: store failed, instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
            break;
        default:
            std::cerr << "unrecognized error type: " << (int)error_type;
    }
    std::cerr << std::endl;

    should_stop = true;
    stop_error = true;
}

extern "C" void sim_commit() {
    inst_cnt++;
}

static int load_file(const char *filename) {
    std::ifstream ifs;
    ifs.open(filename, std::ios::in | std::ios::binary);
    if (!ifs.is_open()) {
        std::cerr << "Unable to open " << filename << std::endl;
        return 1;
    }

    ifs.seekg(0, std::ios::end);
    std::streamsize size = ifs.tellg();
    ifs.seekg(0, std::ios::beg);

    if (size > SIZE) {
        std::cerr << "File (" << filename << ") size " << size << " > max allowed size " << SIZE << std::endl;
        return 1;
    }

    mem = new u8[SIZE];
    if (!ifs.read((char *)mem, size)) {
        std::cerr << "Fail to read " << filename << std::endl;
        ifs.close();
        return 1;
    }

    ifs.close();

    return 0;
}

int main(int argc, char **argv) {
    using namespace std::chrono_literals;

    if (argc <= 1) {
        std::cerr << "Usage: " << argv[0] << " program_name [-summary] [-perip] [-wave]" << std::endl;
        return 1;
    }

    bool summary = false;
    bool wave = false;

    if (argc >= 3) {
        for (int i = 2; i < argc; i++) {
            if (strcmp(argv[i], "-summary") == 0)
                summary = true;
            else if (strcmp(argv[i], "-wave") == 0)
                wave = true;
            else if (strcmp(argv[i], "-perip") == 0)
                sim_perip = true;
        }
    }

    if (load_file(argv[1]))
        return 1;

    VerilatedContext *contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    Vtop *top = new Vtop { contextp };

    VerilatedVcdC *tfp = nullptr;
    if (wave) {
        Verilated::traceEverOn(true);
        tfp = new VerilatedVcdC;
        top->trace(tfp, 99);
        tfp->open("obj_dir/wave.vcd");
    }

    bool clk = false;
    unsigned int rst_cnt = 8;

    if (summary)
        puts("==== SIMULATION START ====");

    auto start_time = std::chrono::high_resolution_clock::now();
    while (!contextp->gotFinish() && !should_stop) {
        contextp->timeInc(1);

        top->clk = clk ? 1 : 0;
        if (rst_cnt != 0) {
            rst_cnt--;
            top->rst = 1;
        } else {
            top->rst = 0;
        }

        clk = !clk;

        if (sim_perip) {
            // update UART queue behaviour
            if (uart_counter >= uart_clk_div * 11) {
                uart_counter = 0;
                if (uart_queue_used > 0) {
                    uart_queue_used--;
                }
            } else {
                uart_counter++;
            }
        }

        top->eval();

        if (wave) {
            tfp->dump(contextp->time());

            // don't dump too quick
            if (contextp->time() >= (1 << 20))
                std::this_thread::sleep_for(50us);
        }
    }
    auto end_time = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> time_elapsed = end_time - start_time;

    if (summary) {
        puts("\n==== SIMULATION SUMMARY ====");

        unsigned int sim_time = contextp->time();
        unsigned int sim_clocks = sim_time / 2;
        std::cout << "  CLOCK COUNT : " << std::right << std::setw(10) << sim_clocks << std::endl;
        std::cout << "  INST COUNT  : " << std::right << std::setw(10) << inst_cnt << "  (CPI: " << (double)sim_clocks / inst_cnt << ")" << std::endl;
        std::cout << "  TIME ELAPSED: " << time_elapsed.count() << " s  (freq: " << sim_clocks / time_elapsed.count() / 1e6 << " MHZ)" << std::endl;
    }

    if (wave) {
        tfp->close();
        delete tfp;
    }

    top->final();
    delete top;
    delete contextp;

    delete[] mem;

    if (stop_error)
        return 1;
    else
        return 0;
}
