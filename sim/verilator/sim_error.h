#ifndef SIM_ERROR_H
#define SIM_ERROR_H

typedef enum {
	ERROR_NONE 		= 0x0,
	ERROR_IFU		= 0x1,
	ERROR_EXU_INVALID	= 0x2,
	ERROR_MAU_LOAD		= 0x3,
	ERROR_MAU_STORE		= 0x4
} error_type_t;

#endif
