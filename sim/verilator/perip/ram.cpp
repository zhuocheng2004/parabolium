
#include <fstream>
#include <sstream>

#include "ram.h"

RAM::RAM() {
	mem = new u8[SIZE];
}

RAM::~RAM() {
	delete[] mem;
}

AXI5LiteResp RAM::read(u32 raddr, u32* rdata) {
	AXI5LiteResp resp = AXI5_DECERR;
	u32 addr = raddr & ~0x3;

	if ((addr & ~MASK) != 0) {
		resp = AXI5_DECERR;
	} else {
		u32 data = mem[addr] + (mem[addr + 1] << 8) + (mem[addr + 2] << 16) + (mem[addr + 3] << 24);
		*rdata = data;
		resp = AXI5_OKAY;
	}

	return resp;
}

AXI5LiteResp RAM::write(u32 waddr, u32 wdata, u8 wmask) {
	AXI5LiteResp resp = AXI5_DECERR;
	u32 addr = waddr & ~0x3;

	if (wmask & 0xf == 0) {
		resp = AXI5_OKAY;
	} else if ((addr & ~MASK) != 0) {
		resp = AXI5_DECERR;
	} else {
		if (wmask & 0x1)
			mem[addr] = wdata & 0xff;
		if (wmask & 0x2)
			mem[addr + 1] = (wdata >> 8) & 0xff;
		if (wmask & 0x4)
			mem[addr + 2] = (wdata >> 16) & 0xff;
		if (wmask & 0x8)
			mem[addr + 3] = (wdata >> 24) & 0xff;

		resp = AXI5_OKAY;
	}

	return resp;
}

void RAM::load_file(const std::string& filename) {
	std::ifstream ifs;
	ifs.open(filename, std::ios::in | std::ios::binary);
	
	ifs.seekg(0, std::ios::end);
	std::streamsize size = ifs.tellg();
	ifs.seekg(0, std::ios::beg);

	if (size > SIZE) {
		std::stringstream ss;
		ss << "File (" << filename << ") size " << size << " > max allowed size " << SIZE;
		throw std::runtime_error(ss.str());
	}

	ifs.read((char*) mem, size);

	ifs.close();
}
