
# Parabolium: a simple RISC-V Chip design

## Features

I design this chip for learning and fun.

Parabolium has one Pab1 core, which supports a core part of the RV32I instruction set including a flat memory model, basic integer operations (except multiplication/division), and load/store operations. 

Memory fences, CSR, ecall/ebreak, and interrupt handling are not supported yet. 

Parabolium and Pab1 are mainly written in [Chisel HDL](https://www.chisel-lang.org/).

### Features of Pab1 Core

- Basic part of RV32I
- Multi-cycle 5-stage
- Flat memory model
- No L1 I/D-cache yet

### Features of the Parabolium SoC

- One Pab1 core
- Supports up to 2G external RAM
- One 8-bit LED output
- One UART controller (only data output implemented) with configurable baud rate
- No L2/L3 cache yet

## Build

To build SystemVerilog from Chisel Scala sources, you need `make` and `sbt` (with Scala toolchain).

To build SystemVerilog, just run
```bash
make
```

The generated Verilog files are in `rtl/generated`, with different builds. (e.g. only a Pab1 Core or the whole SoC Tile? designed for simulation, testing, or synthesis?)

## Simulation

You need a `verilator` simulator and a RISC-V GCC-compatible C/C++ compiler toolchain to build programs.

To simulate the default example, run
```
make sim
```

The default compiler prefix is `riscv32-unknown-elf-`. You can use another prefix by running
```
make sim RISCV_TOOLCHAIN_PREFIX=<your prefix>
```

## Testing

You need `sbt` and `z3` (for formal tests) to run the tests.

To test the pure Chisel design of Pab1, run
```
make test
```

To simulate Parabolium with some real test programs (including a ported version of `riscv-tests`), run
```
make sim_test
```

## Cleaning

You clean project (delete results, logs, intermediate files), run
```
make clean
```

## FPGA

Currently only Gowin FPGA type `GW2A` is supported.

To move generated Verilog files to the FPGA project directory as well as generate some FPGA-specific files, run
```
make fpga
``` 

After that, use a [Gowin IDE](https://www.gowinsemi.com.cn/faq.aspx) and open the project `fpga/gowin/Parabolium_GW2A/Parabolium_GW2A.gprj`. 
Run `Synthesis` and `Place & Route` to generate a bitstream file, and use a Gowin Programmer to program it onto an FPGA board.

Note: I'm currently using the board `Sipeed Tang Primer 20K (Lite)`. For other boards, you need to design different floor plans and pin connections.

Currently, the design can run at 81MHZ (Pab1 clock frequency) on this FPGA board correctly. (failed to run at 108MHZ)

## Thanks

Special thanks to the following projects/organizations. Many ideas and advices are from them.

- [“一生一芯”计划](https://ysyx.oscc.cc/)
- [龙芯3A6000](https://www.loongson.cn/product/show?id=26)
- [香山 RISC-V 处理器](https://gitee.com/OpenXiangShan/XiangShan)
