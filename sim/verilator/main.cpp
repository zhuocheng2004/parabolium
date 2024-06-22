
#include <chrono>
#include <iomanip>
#include <iostream>
#include <thread>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <verilated.h>
#include <verilated_vcd_c.h>

#include "Vtop.h"

#include "perip/perip.h"
#include "sim_error.h"
#include "types.h"

// Peripherals
static Peripherals* perip = nullptr;

// statiscial && debug variables
static bool should_stop = false;
static bool stop_error  = false;

static unsigned int inst_cnt = 0;


extern "C" void sim_read(u32 raddr) {
	perip->read(raddr);
}

extern "C" u8 sim_read_ok(u32 *rdata) {
	return perip->read_ok(rdata);
}

extern "C" void sim_write(u32 waddr, u32 wdata, u8 wmask) {
	perip->write(waddr, wdata, wmask);
}

extern "C" u8 sim_write_ok() {
	return perip->write_ok();
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

int main(int argc, char **argv) {
	using namespace std::chrono_literals;

	if (argc <= 1) {
		std::cerr << "Usage: " << argv[0] << " program_name [-summary] [-perip] [-wave]" << std::endl;
		return 1;
	}

	bool summary	= false;
	bool wave		= false;
	bool sim_perip	= false;

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

	perip = new Peripherals(argv[1], sim_perip);

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

		if (clk && sim_perip) {
			perip->update();
		}

		top->eval();

		if (perip->has_error())
			stop_error = true;

		if (perip->should_stop())
			should_stop = true;

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

	if (stop_error)
		return 1;
	else
		return 0;
}
