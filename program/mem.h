#ifndef MEM_H
#define MEM_H

extern char *heap_start, *heap_end, *heap_ptr;

char* malloc(unsigned int size);

void free_all();

#endif
