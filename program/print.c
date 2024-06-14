
#include "print.h"

void serial_init(void)
{
	// 54MHZ // 9600
	*((unsigned short *) 0xA0002000U) = 5625;
}

void putchar(char ch)
{
	// led
	*((char *) 0xA0001004U) = ch;

	// serial
	*((char *) 0xA0002004U) = ch;
}

void putstr(const char *str)
{
	while (*str)
		putchar(*(str++));
}

static const char HEX_CHARS[] = "0123456789ABCDEF";

void putw(unsigned int n)
{
	for (int i = 28; i >= 0; i -= 4)
		putchar(HEX_CHARS[(n >> i) & 0xf]);
}

void puth(unsigned short n)
{
	putchar(HEX_CHARS[(n >> 12) & 0xf]);
	putchar(HEX_CHARS[(n >> 8) & 0xf]);
	putchar(HEX_CHARS[(n >> 4) & 0xf]);
	putchar(HEX_CHARS[n & 0xf]);
}

void putb(unsigned char n)
{
	putchar(HEX_CHARS[(n >> 4) & 0xf]);
	putchar(HEX_CHARS[n & 0xf]);
}
