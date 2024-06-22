#ifndef PERIP_DEFINES_H
#define PERIP_DEFINEA_H

#include "../types.h"

static inline u8 make_resp(AXI5LiteResp axi_resp, bool ok) {
	return (axi_resp << 1) | (ok ? 0x1 : 0x0);
}

static inline bool resp_ok(u8 resp) {
	return resp & 0x1 != 0;
}

static inline bool resp_good(u8 resp) {
	return resp & 0x1 == 0 || (resp >> 1) == AXI5_OKAY;
}


/* different memory areas */

static const u32 RAM_BASE	= 0x80000000U;

static const u32 MISC_BASE	= 0xA0001000L;

static const u32 STOP		= 0x0;

static const u32 LED		= 0x4;

static const u32 UART_BASE	= 0xA0002000L;

static const u32 UART_CLK_DIV	= 0x0;

static const u32 UART_TX	= 0x4;

#endif
