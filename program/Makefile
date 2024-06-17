
RISCV_GCC	:= ${RISCV_TOOLCHAIN_PREFIX}gcc
RISCV_LD	:= ${RISCV_TOOLCHAIN_PREFIX}ld
RISCV_OBJCOPY	:= ${RISCV_TOOLCHAIN_PREFIX}objcopy
RISCV_GCC_FLAGS	= -march=rv32i -nostdinc -nostdlib -Wno-builtin-declaration-mismatch

RISCV_TEST_DIR	:= riscv-tests-ported
REPORT_DIR	:= report

START_O := start.o

OBJS := math.o mem.o string.o print.o
LIBA := lib.a

all: $(PROG_NAME).bin

define gen-out
$(1).out: link.ld ${START_O} ${LIBA} ${$(1)_OBJS}
	@${MAKE} gen-out-$(1)

.PHONY: gen-out-$(1)
gen-out-$(1):
	${RISCV_LD} -T link.ld ${START_O} ${$(1)_OBJS} ${LIBA} -o $(1).out
endef

${PROG_NAME}_OBJS := main.o
$(eval $(call gen-out,${PROG_NAME}))

TEST_BINS := test1.bin test2.bin

${LIBA}: ${OBJS}
	ar rcs $@ $^

%.bin: %.out
	${RISCV_OBJCOPY} -O binary $^ $@

%.o: %.s
	${RISCV_GCC} ${RISCV_GCC_FLAGS} -c $^ -o $@

%.o: %.c
	${RISCV_GCC} ${RISCV_GCC_FLAGS} -c $^ -o $@


.PHONY: test
test: ${TEST_BINS}
	@mkdir -p $(REPORT_DIR)
	@for bin in $^; do echo ">>>> TEST $${bin}"; ${SIM_EXE} $${bin} -summary 2>&1 | tee $(REPORT_DIR)/$${bin}.txt ; echo ">>>>\n"; done
	@echo "==== RISCV TESTS (PORTED) START ===="
	cd $(RISCV_TEST_DIR) && $(MAKE) && $(MAKE) run
	@mkdir -p $(REPORT_DIR)/riscv_test/
	cp $(RISCV_TEST_DIR)/log/* $(REPORT_DIR)/riscv_test/
	@echo "================================"
	@echo "Please look into dir 'report' for logs"
	@echo "================================"

test1_OBJS := test1.o
$(eval $(call gen-out,test1))

test2_OBJS := test2.o
$(eval $(call gen-out,test2))


.PHONY: clean
clean:
	rm -rf $(REPORT_DIR)
	cd $(RISCV_TEST_DIR) && $(MAKE) clean
	rm -rf *.bin *.out *.a *.o
