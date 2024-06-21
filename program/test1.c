
#include "print.h"

int main()
{
	serial_init();
	
	putstr("Hello World!\n");
	const int N = 20;
	for (int i = 0; i < N; i++) {
		for (int j = 0; j < N-i; j++) putchar(' ');
		for (int j = 0; j < 2*i+1; j++) putchar('*');
		for (int j = 0; j < N-i; j++) putchar(' ');
		putchar('\n');
	}

	return 0;
}
