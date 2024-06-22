#ifndef PERIP_UART_H
#define PERIP_UART_H

#include "../types.h"

typedef class UART {
public:
	inline	UART(int txQueueSize = 0x10, int defaultClkDiv = 0x100) :
		txQueueSize(txQueueSize), clkDiv(defaultClkDiv) {};
	
	inline void setClkDiv(int clkDiv) {
		this->clkDiv = clkDiv;
	}

	// returns true on success
	bool	enqueue(u8 data);
	
	void	update();
private:
	int	txQueueSize;
	int	txQueueUsed = 0;
	int	counter	= 0;
	int	clkDiv;
} UART;

#endif
