
#include <iomanip>
#include <iostream>

#include <stdio.h>

#include "perip.h"
#include "ram.h"

Peripherals::Peripherals(const std::string& filename, bool sim) : ram(), sim(sim) {
	// initialize RAM content with a file
	if (!filename.empty()) {
		ram.load_file(filename);
	}
}

void	Peripherals::read(u32 raddr) {
	if (reading) {
		std::cerr << "ERROR: Another reading is in progress." << std::endl;
		error = true;
	} else {
		this->raddr = raddr & ~0x3;
		reading = true;
	}
}

u8	Peripherals::read_ok(u32* rdata) {
	if (!reading) {
		std::cerr << "ERROR: Reading not started." << std::endl;
		error = true;
		return make_resp(AXI5_DECERR, false);
	} else {
		u8 resp = make_resp(AXI5_DECERR, false);

		if ((raddr & ~RAM::MASK) == RAM_BASE) {
			resp = make_resp(ram.read(raddr & RAM::MASK, rdata), true);
		}

		if (!resp_good(resp)) {
			std::cerr << "WARNING: LOAD @ " << std::hex << std::setw(8) << raddr << std::endl;
		}

		if (resp_ok(resp))
			reading = false;
		
		return resp;
	}
}

void	Peripherals::write(u32 waddr, u32 wdata, u8 wmask) {
	if (writing) {
		std::cerr << "ERROR: Another writing is in progress." << std::endl;
		error = true;
	} else {
		this->waddr = waddr & ~0x3;
		this->wdata = wdata;
		this->wmask = wmask;
		writing = true;
	}
}

u8	Peripherals::write_ok() {
	if (!writing) {
		std::cerr << "ERROR: Writing not started." << std::endl;
		error = true;
		return make_resp(AXI5_DECERR, false);
	} else {
		u8 resp = make_resp(AXI5_DECERR, false);

		if ((wmask & 0xf) == 0) {
			resp = make_resp(AXI5_OKAY, true);
		} else if ((waddr & ~RAM::MASK) == RAM_BASE) {
			resp = make_resp(ram.write(waddr & RAM::MASK, wdata, wmask), true);
		} else if (waddr == MISC_BASE + STOP) {
			if (wmask & 0x1 != 0 && wdata & 0xff != 0)
				stop = true;
			resp = make_resp(AXI5_OKAY, true);
		} else if (waddr == MISC_BASE + LED) {
			if (wmask & 0x1 != 0) {
				putchar(wdata & 0xff);
				fflush(stdout);
			}
			resp = make_resp(AXI5_OKAY, true);
		} else if (waddr == UART_BASE + UART_CLK_DIV) {
			if (wmask & 0x3 == 0x3)
				uart.setClkDiv(wdata & 0xffff);
			resp = make_resp(AXI5_OKAY, true);
		} else if (waddr == UART_BASE + UART_TX) {
			if (wmask & 0x1 != 0 && sim)
				resp = make_resp(AXI5_OKAY, uart.enqueue(wdata & 0xff));
			else
				resp = make_resp(AXI5_OKAY, true);
		}

		if (!resp_good(resp)) {
			std::cerr << "WARNING: SAVE @ " << std::hex << std::setw(8) << waddr << ", data=" << std::hex << wdata << ", mask=" << std::hex << (u32) wmask << std::endl;
		}

		if (resp_ok(resp))
			writing = false;
		
		return resp;
	}
}

void	Peripherals::update() {
	if (sim) {
		uart.update();
	}
}
