
#include "print.h"
#include "string.h"

void memset(char *ptr, char x, unsigned int count) {
	for (int i = 0; i < count; i++)
		ptr[i] = x;
}

void memcpy(char *dst, const char *src, unsigned int count) {
	for (int i = 0; i < count; i++) dst[i] = src[i];
}

int strlen(const char *str) {
	int n = 0;
	while (*(str++)) n++;
	return n;
}
