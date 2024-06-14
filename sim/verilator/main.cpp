
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

#include "sim_error.h"
#include "types.h"

static const unsigned int SIZE = 1 << 14;
static const unsigned int MASK = SIZE - 1;
static const unsigned int BASE = 0x80000000U;

static u8 *mem;

static bool should_stop = false;

static unsigned int inst_cnt = 0;

extern "C" u32 sim_ram_read(u32 raddr)
{
	if (raddr >= BASE && raddr < BASE + SIZE)
	{
		u32 addr = raddr & MASK & ~0x3u;
		return mem[addr] + (mem[addr + 1] << 8) + (mem[addr + 2] << 16) + (mem[addr + 3] << 24);
	}
	else
	{
		std::cerr << "WARNING: LOAD @ " << raddr << std::endl;
		return 0;
	}
}

extern "C" void sim_ram_write(u32 waddr, u32 wdata, u8 wmask)
{
	if (!(wmask & 0xf))
		return;

	else if (waddr >= BASE && waddr < BASE + SIZE)
	{
		u32 addr = waddr & MASK & ~0x3u;
		if (wmask & 0x1)
			mem[addr] = wdata & 0xff;
		if (wmask & 0x2)
			mem[addr + 1] = (wdata >> 8) & 0xff;
		if (wmask & 0x4)
			mem[addr + 2] = (wdata >> 16) & 0xff;
		if (wmask & 0x8)
			mem[addr + 3] = (wdata >> 24) & 0xff;
	}
	else
	{
		std::cerr << "WARNING: SAVE @ " << waddr << ", data=" << wdata << ", mask=" << (u32)wmask << std::endl;
	}
}

extern "C" void sim_stop()
{
	should_stop = true;
}

extern "C" void sim_error(u8 error_type, u32 info0)
{
	std::cerr << "ERROR: ";
	switch (error_type)
	{
	case ERROR_NONE:
		std::cerr << "error reported on success?";
		break;
	case ERROR_IFU:
		std::cerr << "error fetching instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
		break;
	case ERROR_EXU_INVALID:
		std::cerr << "invalid instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
		break;
	case ERROR_MAU_LOAD:
		std::cerr << "load failed by instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
		break;
	case ERROR_MAU_STORE:
		std::cerr << "store failed by instruction @ " << std::hex << std::setw(8) << std::setfill('0') << info0;
		break;
	default:
		std::cerr << "unrecognized error type: " << (int)error_type;
		break;
	}
	std::cerr << std::endl;

	should_stop = true;
}

extern "C" void sim_commit()
{
	inst_cnt++;
}

static int load_file(const char *filename)
{
	std::ifstream ifs;
	ifs.open(filename, std::ios::in | std::ios::binary);
	if (!ifs.is_open())
	{
		std::cerr << "Unable to open " << filename << std::endl;
		return 1;
	}

	ifs.seekg(0, std::ios::end);
	std::streamsize size = ifs.tellg();
	ifs.seekg(0, std::ios::beg);

	if (size > SIZE)
	{
		std::cerr << "File (" << filename << ") size " << size << " > max allowed size " << SIZE << std::endl;
		return 1;
	}

	mem = new u8[SIZE];
	if (!ifs.read((char *)mem, size))
	{
		std::cerr << "Fail to read " << filename << std::endl;
		ifs.close();
		return 1;
	}

	ifs.close();

	return 0;
}

int main(int argc, char **argv)
{
	using namespace std::chrono_literals;

	if (argc <= 1)
	{
		std::cerr << "Usage: " << argv[0] << " program_name [-summary] [-wave]" << std::endl;
		return 1;
	}

	bool summary = false;
	bool wave = false;

	if (argc >= 3)
	{
		for (int i = 2; i < argc; i++)
		{
			if (strcmp(argv[i], "-summary") == 0)
				summary = true;
			if (strcmp(argv[i], "-wave") == 0)
				wave = true;
		}
	}

	if (load_file(argv[1]))
		return 1;

	VerilatedContext *contextp = new VerilatedContext;
	contextp->commandArgs(argc, argv);
	Vtop *top = new Vtop{contextp};

	VerilatedVcdC *tfp = nullptr;
	if (wave)
	{
		Verilated::traceEverOn(true);
		tfp = new VerilatedVcdC;
		top->trace(tfp, 99);
		tfp->open("obj_dir/wave.vcd");
	}

	bool clk = false;
	unsigned int rst_cnt = 8;

	auto start_time = std::chrono::high_resolution_clock::now();
	while (!contextp->gotFinish() && !should_stop)
	{
		contextp->timeInc(1);

		top->clk = clk ? 1 : 0;
		if (rst_cnt != 0)
		{
			rst_cnt--;
			top->rst = 1;
		}
		else
		{
			top->rst = 0;
		}

		clk = !clk;

		top->eval();

		if (wave)
		{
			tfp->dump(contextp->time());

			// don't dump too quick
			if (contextp->time() >= (1 << 20))
				std::this_thread::sleep_for(50us);
		}
	}
	auto end_time = std::chrono::high_resolution_clock::now();
	std::chrono::duration<double> time_elapsed = end_time - start_time;

	if (summary)
	{
		puts("\n==== SIMULATION SUMMARY ====");

		unsigned int sim_time = contextp->time();
		unsigned int sim_clocks = sim_time / 2;
		std::cout << "  CLOCK COUNT : " << std::right << std::setw(10) << sim_clocks << std::endl;
		std::cout << "  INST COUNT  : " << std::right << std::setw(10) << inst_cnt << "  (CPI: " << (double)sim_clocks / inst_cnt << ")" << std::endl;
		std::cout << "  TIME ELAPSED: " << time_elapsed.count() << " s  (freq: " << sim_clocks / time_elapsed.count() / 1e6 << " MHZ)" << std::endl;
	}

	if (wave)
	{
		tfp->close();
		delete tfp;
	}

	top->final();
	delete top;
	delete contextp;

	delete[] mem;

	return 0;
}
