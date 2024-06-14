#ifndef MD5_H
#define MD5_H

#include "string.h"
#include "print.h"

typedef unsigned char uint8_t;
typedef unsigned int uint32_t;
typedef unsigned int size_t;
typedef unsigned long long uint64_t;

typedef struct{
    uint64_t size;        // Size of input in bytes
    uint32_t buffer[4];   // Current accumulation of hash
    uint8_t input[64];    // Input to be used in the next step
    uint8_t digest[16];   // Result of algorithm
}MD5Context;

void md5Init(MD5Context *ctx);
void md5Update(MD5Context *ctx, const uint8_t *input, size_t input_len);
void md5Finalize(MD5Context *ctx);
void md5Step(uint32_t *buffer, uint32_t *input);

void md5String(const char *input, uint8_t *result);

#endif
