
ENTRY(_start)
SECTIONS {
	. = 0x80000000;

	.text : {
		_stext = .;
		*(.text);
	}

	_etext = .;
	. = ALIGN(0x1000);

	.rodata : {
		*(.rodata)
	}

	. = ALIGN(0x1000);

	.bss : { }

	. = ALIGN(0x1000);

	_end = .;
}
