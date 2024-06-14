
export RTL_DIR		:= $(PWD)/rtl
export RTL_GEN_DIR	:= $(RTL_DIR)/generated
RTL_GEN_FILES	:= $(RTL_GEN_DIR)/synth/Core.sv $(RTL_GEN_DIR)/sim/Core.sv
RTL_TEST_DIR	:= $(RTL_DIR)/test_run_dir

export SIM_DIR		:= $(PWD)/sim

SBT		?= sbt
VERILATOR	?= verilator

export SBT VERILATOR

.PHONY: all
all: $(RTL_GEN_FILES)

SCALA_SRCS	:= $(shell find $(RTL_DIR)/src/main -name '*.scala')

$(RTL_GEN_FILES): $(SCALA_SRCS)
	cd $(RTL_DIR) && $(SBT) run
	@echo "Generated verilog design: $(RTL_GEN_FILES)"

.PHONY: test
test:
	cd $(RTL_DIR) && $(SBT) test

export SIM_EXE_NAME	?= sim_exe
SIM_EXE	:= $(SIM_DIR)/verilator/obj_dir/$(SIM_EXE_NAME)

.PHONY: sim	# verilator
sim: $(SIM_EXE)
	$(MAKE) -C $(SIM_DIR)/verilator sim

$(SIM_EXE): $(RTL_GEN_FILES)
	$(MAKE) -C $(SIM_DIR)/verilator

.PHONY: clean
clean:
	rm -rf ${RTL_GEN_DIR} ${RTL_TEST_DIR}
	$(MAKE) -C $(SIM_DIR)/verilator clean
	cd $(RTL_DIR) && $(SBT) clean 
