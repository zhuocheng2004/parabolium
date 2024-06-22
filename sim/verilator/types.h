#ifndef TYPES_H
#define TYPES_H

typedef unsigned char	u8;
typedef unsigned int	u32;


/* AXI5-Lite response signals */

typedef enum {
	AXI5_OKAY	= 0x0,
	AXI5_SLVERR	= 0x2,	// unsupported read/write; failed action; device powered down
	AXI5_DECERR	= 0x3	// illegal address
} AXI5LiteResp;

#endif
