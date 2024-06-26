
# Parabolium: an Open Simple RISC-V Processor Series

I design this chip mainly for learning and fun. I am currently working on the `Parabolium 1000` processor.


## Features

`Pab1` core and `Parabolium` series RTL codes are mainly written in [Chisel HDL](https://www.chisel-lang.org/).

`Parabolium` is a series of processors that mainly uses `Pab` series RISC-V cores. For the version naming conventions, see [Naming](docs/Naming.md).

`Parabolium 1000` is a SoC processor design that has one `Pab1` core, which supports the `RV32I` Base Integer Instruction Set. It is still in progress.

Memory fences, `Zifencei` extension, CSR, ecall/ebreak, interrupt handling, etc. are not supported yet or trivially implemented. 

`Parabolium 1000` uses Gowin FPGA type `GW2A` as the backend.

### Features of `Pab1` Core

- `RV32I` Base Integer Instruction Set
- little-endian byte ordering
- multi-cycle, 5-stage
- trivial implementation for memory ordering (`FENCE` instruction) since not needed (will change in the future)
- trivial implementation for `Zifencei` extension (`FENCE.I` instruction) since not needed (will change after I/D-caches are added)
- Only machine-mode is implemented. (TODO)
- ?KB L1 I-Cache/D-Cache (TODO)

### Features of `Parabolium 1000` Processor

- one `Pab1` core
- supports up to maximum of 2G external RAM (TODO)
- 4MB SPI NOR Flash XIP boot (TODO)
- one 8-bit LED signal output
- one UART output controller with configurable baud rate
- ?KB L2 Cache (TODO)


## Build

To build Verilog (SystemVerilog) from Chisel Scala sources, you need `make` and `sbt` (with Scala toolchain) tools.

To build Verilog, just run
```
make
```

The generated Verilog files will be in `rtl/generated`, with different build variants for different purposes (e.g. simulation, synthesis, etc.). 


## Simulation

Currently, only the `Pab1` core of `Parabolium 1000` will be simulated at the RTL level. Peripherals, caches, and memory access are simulated using C++. The simulation console shows the 8-bit LED signal data. 

I'm trying to make it able to run a ported version of `RT-Thread`. (in progess) 

You need a `verilator` simulator (as well as a host C/C++ program compiler) and a RISC-V GCC compiler toolchain to build test programs.

To build test programs and simulate with the default example on `Parabolium 1000`, run
```
make sim
```

The default RISC-V GCC compiler prefix is `riscv32-unknown-elf-`. You can use another prefix by running
```
make sim RISCV_TOOLCHAIN_PREFIX=<your prefix>
```


## Testing

You need `sbt` etc. and `z3` (for formal tests) in order to run all the tests.

To test the pure Chisel design of the `Pab1` core, run
```
make test
```

To simulate `Parabolium 1000` with some more test programs (including a ported version of `riscv-tests`), run
```
make sim_test
```


## Cleaning

You clean project (delete results, logs, intermediate files), run
```
make clean
```

## FPGA

`Parabolium 1000` uses Gowin FPGA type `GW2A` as the backend.

This project includes a Gowin FPGA project designed for board [Sipeed Tang Primer 20K (Lite)](https://www.gowinsemi.com.cn/clients_view.aspx?TypeId=21&Id=960). It uses a 27MHZ oscillator on the board as the main clock input (internally converted to 81MHZ using PLL), a button as the reset signal input, and a 1-bit UART TX signal output, an 8-bit LED signal output. Currently, I am using 16KB BSRAM on the chip as the main memory, and the program binary is pre-loaded into BSRAM before executing. In the future, the 128MB external DDR3 memory will be used, and SPI NOR Flash XIP boot will be supported.

For other boards with `GW2A`, you need to design different floor plans and pin connections according to those boards.

To prepare for the FPGA project (move generated Verilog files, generate some helper files), run
```
make fpga
``` 

After that, use a [Gowin IDE](https://www.gowinsemi.com.cn/faq.aspx) to open the project `fpga/gowin/Parabolium_1000/Parabolium_1000.gprj`. 
Run `Synthesis` and `Place & Route` in the IDE to generate a bitstream file, and use a Gowin Programmer to program it onto the FPGA board.

Currently, `Parabolium 1000` can run (the default test program) at 81MHZ on `Sipeed Tang Primer 20K (Lite)` successfully. (failed to run at 108MHZ)


## Thanks

Special thanks to the following projects/organizations. Many ideas are from them.

- [“一生一芯”计划](https://ysyx.oscc.cc/)
- [龙芯3A6000](https://www.loongson.cn/product/show?id=26)
- [STM32 MCU](https://www.st.com/en/microcontrollers-microprocessors/stm32-32-bit-arm-cortex-mcus.html)
- [香山 RISC-V 处理器](https://gitee.com/OpenXiangShan/XiangShan)
