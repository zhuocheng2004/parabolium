
char *heap_start, *heap_end, *heap_ptr;

char* malloc(unsigned int size) {
	if (heap_ptr + size >= heap_end)
		return (void*) 0;

	char *ptr = heap_ptr;
	heap_ptr += size;
	return ptr;
}

void free_all() {
	heap_ptr = heap_start;
}

