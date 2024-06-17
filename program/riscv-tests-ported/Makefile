#=======================================================================
# Makefile for riscv-tests/isa
#-----------------------------------------------------------------------

XLEN ?= 32

src_dir := .

ifeq ($(XLEN),64)
include $(src_dir)/rv64ui/Makefrag
endif
include $(src_dir)/rv32ui/Makefrag

default: all

LOG_DIR	:= log

#--------------------------------------------------------------------
# Build rules
#--------------------------------------------------------------------

RISCV_PREFIX ?= riscv$(XLEN)-unknown-elf-
RISCV_GCC ?= $(RISCV_PREFIX)gcc
RISCV_GCC_OPTS ?= -static -mcmodel=medany -fvisibility=hidden -nostdlib -nostartfiles
RISCV_OBJDUMP ?= $(RISCV_PREFIX)objdump --disassemble-all --disassemble-zeroes --section=.text --section=.text.startup --section=.text.init --section=.data
RISCV_SIM ?= ~/workplace/Parabolium/sim/verilator/obj_dir/sim_exe

vpath %.S $(src_dir)

#------------------------------------------------------------
# Build assembly tests

%.dump: %
	$(RISCV_OBJDUMP) $< > $@

%.bin: %
	$(RISCV_PREFIX)objcopy -O binary $< $@

%.out: %.out32

%.out32: %.bin
	@mkdir -p $(LOG_DIR)
	@echo ">>>> Testing $<"
	-@if $(RISCV_SIM) $< > .tmp_out 2>&1; then echo "P" > .tmp_pf; else echo "F" > .tmp_pf; fi;
	@if grep F .tmp_pf > /dev/null || grep -E "FAIL|ERROR" .tmp_out > /dev/null; then echo -n "\033[31m"; else echo -n "\033[32m"; fi;
	@cat .tmp_out | tee $(LOG_DIR)/$<.txt
	@echo "\033[0m"

define compile_template

$$($(1)_p_tests): $(1)-p-%: $(1)/%.S link.ld riscv_test.h test_macros.h
	$$(RISCV_GCC) $(2) $$(RISCV_GCC_OPTS) -I$(src_dir) -T$(src_dir)/link.ld $$< -o $$@
$(1)_tests += $$($(1)_p_tests)

$(1)_tests_dump = $$(addsuffix .dump, $$($(1)_tests))

$(1): $$($(1)_tests_dump)

.PHONY: $(1)

COMPILER_SUPPORTS_$(1) := $$(shell $$(RISCV_GCC) $(2) -c -x c /dev/null -o /dev/null 2> /dev/null; echo $$$$?)

ifeq ($$(COMPILER_SUPPORTS_$(1)),0)
tests += $$($(1)_tests)
endif

endef

$(eval $(call compile_template,rv32ui,-march=rv32g -mabi=ilp32))
ifeq ($(XLEN),64)
$(eval $(call compile_template,rv64ui,-march=rv64g -mabi=lp64))
endif

tests_dump = $(addsuffix .dump, $(tests))
tests_bin = $(addsuffix .bin, $(tests))
tests_hex = $(addsuffix .hex, $(tests))
tests_out = $(addsuffix .out, $(filter rv64%,$(tests)))
tests32_out = $(addsuffix .out32, $(filter rv32%,$(tests)))

run: $(tests_out) $(tests32_out)

junk += $(tests) $(tests_dump) $(tests_bin) $(tests_hex) $(tests_out) $(tests32_out)

#------------------------------------------------------------
# Default

all: $(tests_bin)

#------------------------------------------------------------
# Clean up

clean:
	rm -rf $(LOG_DIR)
	rm -rf $(junk) .tmp_out
