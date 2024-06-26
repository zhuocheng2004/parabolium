
.section	.text

.globl		_start
_start:
	# setup stack
	li		sp, 0x80003ffc

	# setup heap
	la		a0, _end
	sw		a0, heap_start, a1
	sw		a0, heap_ptr, a1
	li		a1, 0x1000
	add		a0, a0, a1
	sw		a0, heap_end, a1

	# go to main
	jal		main

	# stop simulation (no effect if not simulation)
	li		a0, 0xA0001000
	li		a1, 0x1
	sb		a1, 0(a0)
	
	# infinite loop
die:
	j		die
