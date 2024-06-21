
export RTL_DIR		:= $(PWD)/rtl
export RTL_GEN_DIR	:= $(RTL_DIR)/generated
RTL_GEN_FILES		:= $(RTL_GEN_DIR)/sim/Core.sv $(RTL_GEN_DIR)/synth/Tile.sv
RTL_TEST_DIR		:= $(RTL_DIR)/test_run_dir

export SIM_DIR		:= $(PWD)/sim

export PROG_DIR		:= $(PWD)/program

export SIM_EXE_NAME	?= sim_exe
export SIM_EXE		:= $(SIM_DIR)/verilator/obj_dir/$(SIM_EXE_NAME)

export PROG_NAME	:= main
export SIM_BIN		:= $(PROG_DIR)/$(PROG_NAME).bin

export FPGA_PROJECT_DIR	:= $(PWD)/fpga/gowin/Parabolium_GW2A
FPGA_TILE		:= $(FPGA_PROJECT_DIR)/gen/Tile.sv
FPGA_BIN		:= $(FPGA_PROJECT_DIR)/gen/data.bin


GTKWAVE		?= gtkwave
PYTHON3		?= python3
SBT		?= sbt
VALGRIND	?= valgrind
VERILATOR	?= verilator

export RISCV_TOOLCHAIN_PREFIX	?= riscv32-unknown-elf-

export GTKWAVE PYTHON3 SBT VALGRIND VERILATOR


.PHONY: all
all: $(RTL_GEN_FILES)

SCALA_SRCS	:= $(shell find $(RTL_DIR)/src/main -name '*.scala')

$(RTL_GEN_FILES): $(SCALA_SRCS)
	cd $(RTL_DIR) && $(SBT) run
	@echo "Generated verilog design: $(RTL_GEN_FILES)"


.PHONY: sim	# verilator
sim: $(SIM_EXE) $(SIM_BIN)
	$(MAKE) -C $(SIM_DIR)/verilator sim

$(SIM_EXE): $(RTL_GEN_FILES) FORCE
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
sim_test: $(SIM_EXE)
	$(MAKE) -C $(PROG_DIR) test


$(FPGA_TILE): $(RTL_GEN_FILES)
	@mkdir -p $(dir $@)
	cp $(RTL_GEN_DIR)/synth/Tile.sv $@

$(FPGA_BIN): $(SIM_BIN)
	@mkdir -p $(dir $@)
	cp $(SIM_BIN) $@

.PHONY: fpga
fpga: $(FPGA_TILE) $(FPGA_BIN) FORCE
	$(MAKE) -C $(FPGA_PROJECT_DIR)

.PHONY: clean
clean:
	rm -rf $(FPGA_PROJECT_DIR)/gen
	$(MAKE) -C $(PROG_DIR) clean
	$(MAKE) -C $(SIM_DIR)/verilator clean
	rm -rf ${RTL_GEN_DIR} ${RTL_TEST_DIR}
	cd $(RTL_DIR) && $(SBT) clean 

.PHONY: FORCE
FORCE:
