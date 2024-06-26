
GEN_DIR		:= gen
SCRIPT_DIR	:= ../scripts

.PHONY: all
all: $(GEN_DIR)/ram.v

$(GEN_DIR)/ram.v: $(SCRIPT_DIR)/gen_ram.py $(GEN_DIR)/data.bin
	$(PYTHON3) $(SCRIPT_DIR)/gen_ram.py $(GEN_DIR)/data.bin > $@

.PHONY: clean
clean:
	$(MAKE) -C sim clean
	rm -rf $(GEN_DIR)
