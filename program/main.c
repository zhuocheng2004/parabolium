
#include "mem.h"
#include "print.h"

int _gcd(int a, int b)
{
	if (a == 0)
		return b;
	else if (b == 0)
		return a;
	else if (a >= b)
		return _gcd(a - b, b);
	else if (a < b)
		return _gcd(a, b - a);
	else
		return 0;
}

int gcd(int a, int b)
{
	a = a < 0 ? -a : a;
	b = b < 0 ? -b : b;
	return _gcd(a, b);
}

int main()
{
	serial_init();
	
	putstr("Hello World!\n");
	putstr("gcd(88020, 288096): ");
	puth(gcd(88020, 288096));
	putchar('\n');

	putstr("heap_start: ");
	putw((unsigned int)heap_start);
	putstr(", heap_end: ");
	putw((unsigned int)heap_end);
	putchar('\n');

	return 0;
}
