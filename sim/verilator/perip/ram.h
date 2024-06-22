#ifndef PERIP_RAM_H
#define PERIP_RAM_H

#include <string>

#include "../types.h"

typedef class RAM {
public:
	static const unsigned int SHIFT	= 14;
	static const unsigned int SIZE	= 1U << SHIFT;	// in bytes
	static const unsigned int MASK	= SIZE - 1;

	RAM();

	~RAM();

	AXI5LiteResp	read(u32 raddr, u32* rdata);

	AXI5LiteResp	write(u32 waddr, u32 wdata, u8 wmask);
	
	// fill RAM with file content
	void		load_file(const std::string& filename);

private:
	u8*	mem;
} RAM;

#endif
