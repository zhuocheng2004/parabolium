
export SIM_OBJ_DIR	:= $(SIM_DIR)/verilator/obj_dir
export SIM_EXE		:= $(SIM_OBJ_DIR)/$(SIM_EXE_NAME)

CPP_SRCS	:= main.cpp
CPPINC_SRCS	:= types.h
VERILOG_SRCS	:= top.v
VERILOG_SRCS	+= $(RTL_GEN_DIR)/sim/Core.sv

.PHONY: all
all: $(SIM_EXE)

$(SIM_EXE): $(VERILOG_SRCS) $(CPP_SRCS) $(CPPINC_SRCS)
	$(VERILATOR) -cc -exe --trace --build -j $(shell nproc) -O3 --top top --clk clk $(CPP_SRCS) $(VERILOG_SRCS) +define+VERILATOR=1 -o ${SIM_EXE_NAME}

.PHONY: sim
sim: $(SIM_EXE) $(SIM_BIN)
	$(SIM_EXE) $(SIM_BIN) -summary # -wave

.PHONY: wave
wave:
	$(GTKWAVE) $(SIM_OBJ_DIR)/wave.vcd

.PHONY: clean
clean:
	rm -rf $(SIM_OBJ_DIR)
