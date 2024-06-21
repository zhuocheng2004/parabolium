
GEN_DIR		:= $(FPGA_PROJECT_DIR)/gen
SCRIPT_DIR	:= $(FPGA_PROJECT_DIR)/scripts

all: $(GEN_DIR)/ram.v

$(GEN_DIR)/ram.v: $(SCRIPT_DIR)/gen.py $(GEN_DIR)/data.bin
	$(PYTHON3) $(SCRIPT_DIR)/gen.py $(GEN_DIR)/data.bin > $@
