
export RTL_DIR		:= $(PWD)/rtl
export RTL_GEN_DIR	:= $(RTL_DIR)/generated
RTL_GEN_FILES	:= $(RTL_GEN_DIR)/synth/Core.sv $(RTL_GEN_DIR)/sim/Core.sv
RTL_TEST_DIR	:= $(RTL_DIR)/test_run_dir

export SIM_DIR		:= $(PWD)/sim

export PROG_DIR		:= $(PWD)/program

FPGA_PROJECT_DIR	:= $(PWD)/fpga/gowin/Parabolium_GW2A

GTKWAVE		?= gtkwave
SBT		?= sbt
VERILATOR	?= verilator

export RISCV_TOOLCHAIN_PREFIX	?= riscv32-unknown-elf-

export GTKWAVE SBT VERILATOR

.PHONY: all
all: $(RTL_GEN_FILES) $(FPGA_PROJECT_DIR)/gen/Core.sv

SCALA_SRCS	:= $(shell find $(RTL_DIR)/src/main -name '*.scala')

$(RTL_GEN_FILES): $(SCALA_SRCS)
	cd $(RTL_DIR) && $(SBT) run
	@echo "Generated verilog design: $(RTL_GEN_FILES)"

$(FPGA_PROJECT_DIR)/gen/Core.sv: $(RTL_GEN_FILES)
	cp $(RTL_GEN_DIR)/synth/Core.sv $@

export SIM_EXE_NAME	?= sim_exe
export SIM_EXE		:= $(SIM_DIR)/verilator/obj_dir/$(SIM_EXE_NAME)

export PROG_NAME	:= main
export SIM_BIN		:= $(PROG_DIR)/$(PROG_NAME).bin

.PHONY: sim	# verilator
sim: $(SIM_EXE) $(SIM_BIN)
	$(MAKE) -C $(SIM_DIR)/verilator sim

$(SIM_EXE): $(RTL_GEN_FILES)
	$(MAKE) -C $(SIM_DIR)/verilator

$(SIM_BIN): FORCE
	$(MAKE) -C $(PROG_DIR)

.PHONY: wave
wave:
	$(MAKE) -C $(SIM_DIR)/verilator wave

.PHONY: test
test:
	cd $(RTL_DIR) && $(SBT) test

.PHONY: sim_test
sim_test:
	$(MAKE) -C $(PROG_DIR) test

.PHONY: clean
clean:
	$(MAKE) -C $(PROG_DIR) clean
	$(MAKE) -C $(SIM_DIR)/verilator clean
	rm -rf ${RTL_GEN_DIR} ${RTL_TEST_DIR}
	cd $(RTL_DIR) && $(SBT) clean 

.PHONY: FORCE
FORCE:
