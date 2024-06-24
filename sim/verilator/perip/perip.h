#ifndef PERIP_H
#define PERIP_H

#include <string>

#include "defines.h"
#include "ram.h"
#include "uart.h"

typedef class Peripherals {
public:
	// The file content will be used to fill initial RAM
	// If `sim` is true, we will simulate some more real hardware behaviors like delays.
	// If `sim` is false (default), we will use simple simulation that is much faster.
	Peripherals(const std::string& filename = "", bool sim = false);

	// Call to start a reading action.
	void 	read(u32 raddr);

	// If bit[0] is set, reading has finished.
	// Bit[2:1] is reading AXI5-Lite response.
	u8	read_ok(u32* rdata);

	// Call to start a writing action.
	void	write(u32 waddr, u32 wdata, u8 wmask);

	// similar to read_ok();
	u8	write_ok();

	// this should be called each clock, in order to simulate
	// hardware behaviors like delays.
	void	update();

	// fatal error?
	inline bool has_error() { return error; };

	// should stop simulation?
	inline bool should_stop() { return stop; }

private:
	// Simulate more real hardware?
	bool	sim = false;

	// Is a reading action in progress?
	bool	reading = false;
	u32	raddr	= 0;

	// Is a writing action in progress?
	bool	writing = false;
	u32	waddr	= 0;
	u32	wdata	= 0;
	u32	wmask	= 0;

	// Fatal errors occurred.
	u32	error	= false;

	// should stop simulation.
	u32	stop	= false;

	// The RAM
	RAM	ram;

	// UART simulator
	UART	uart;
} Peripherals;

#endif
