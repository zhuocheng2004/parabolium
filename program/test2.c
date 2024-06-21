
#include "print.h"
#include "string.h"

int main() {
	serial_init();
	
	const int N = 4000;
	char isPrime[N+1];

	memset(isPrime, 1, N+1);

	for (int p = 2; p * p <= N; p++) {
		if (!isPrime[p]) continue;
		for (int i = 2*p; i <= N; i += p)
			isPrime[i] = 0;
	}

	for (int i = 2; i <= N; i++) {
		if (isPrime[i]) {
			puth(i);
			putchar(' ');
		}
	}
	putchar('\n');

	return 0;
}
