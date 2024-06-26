
VSRC	:= tb.v lib.v help.v ../src/top.v ../gen/Tile.sv ../gen/ram.v

NAME ?= main

.PHONY: all
all: exe

exe: ${VSRC}
	iverilog -g2009 -s tb -o $@ $^

.PHONY: sim
sim: exe
	vvp $<

.PHONY: wave
wave:
	gtkwave wave.vcd

.PHONY: clean
clean:
	rm -f exe *.vcd *.bin *.hex

FORCE:
