
#include "uart.h"

bool UART::enqueue(u8 data) {
	if (txQueueUsed < txQueueSize) {
		txQueueUsed++;
		return true;
	} else {
		return false;
	}
}

void UART::update() {
	if (txQueueUsed > 0) {
		if (counter >= clkDiv * 11) {
			counter = 0;
			txQueueUsed--;
		} else {
			counter++;
		}
	}
}
