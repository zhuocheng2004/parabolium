
#include "mem.h"
#include "print.h"
#include "string.h"

int _gcd(int a, int b) {
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

int gcd(int a, int b) {
	a = a < 0 ? -a : a;
	b = b < 0 ? -b : b;
	return _gcd(a, b);
}

void show_triangle() {
	const int N = 20;
	for (int i = 0; i < N; i++) {
		for (int j = 0; j < N-i; j++) putchar(' ');
		for (int j = 0; j < 2*i+1; j++) putchar('*');
		//for (int j = 0; j < N-i; j++) putchar(' ');
		putchar('\n');
	}
}

void show_primes() {
	const int N = 4000;
	char isPrime[N+1];

	memset(isPrime, 1, N+1);

	for (int p = 2; p * p <= N; p++) {
		if (!isPrime[p]) continue;
		for (int i = 2*p; i <= N; i += p)
			isPrime[i] = 0;
	}

	putstr("Primes: \n");
	for (int i = 2; i <= N; i++) {
		if (isPrime[i]) {
			puth(i);
			putchar(' ');
		}
	}
	putchar('\n');
}

int main() {
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

	show_triangle();

	show_primes();

	return 0;
}
