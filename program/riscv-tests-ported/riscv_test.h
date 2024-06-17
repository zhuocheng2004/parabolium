// See LICENSE for license details.

#ifndef _ENV_PHYSICAL_SINGLE_CORE_H
#define _ENV_PHYSICAL_SINGLE_CORE_H

#include "encoding.h"

//-----------------------------------------------------------------------
// Begin Macro
//-----------------------------------------------------------------------

#define RVTEST_RV64U                                                    \
  .macro init;                                                          \
  .endm

#define RVTEST_RV64UF                                                   \
  .macro init;                                                          \
  RVTEST_FP_ENABLE;                                                     \
  .endm

#define RVTEST_RV64UV                                                   \
  .macro init;                                                          \
  RVTEST_VECTOR_ENABLE;                                                 \
  .endm

#define RVTEST_RV32U                                                    \
  .macro init;                                                          \
  .endm

#define RVTEST_RV32UF                                                   \
  .macro init;                                                          \
  RVTEST_FP_ENABLE;                                                     \
  .endm

#define RVTEST_RV32UV                                                   \
  .macro init;                                                          \
  RVTEST_VECTOR_ENABLE;                                                 \
  .endm

#define RVTEST_RV64M                                                    \
  .macro init;                                                          \
  RVTEST_ENABLE_MACHINE;                                                \
  .endm

#define RVTEST_RV64S                                                    \
  .macro init;                                                          \
  RVTEST_ENABLE_SUPERVISOR;                                             \
  .endm

#define RVTEST_RV32M                                                    \
  .macro init;                                                          \
  RVTEST_ENABLE_MACHINE;                                                \
  .endm

#define RVTEST_RV32S                                                    \
  .macro init;                                                          \
  RVTEST_ENABLE_SUPERVISOR;                                             \
  .endm

#if __riscv_xlen == 64
# define CHECK_XLEN li a0, 1; slli a0, a0, 31; bgez a0, 1f; RVTEST_PASS; 1:
#else
# define CHECK_XLEN li a0, 1; slli a0, a0, 31; bltz a0, 1f; RVTEST_PASS; 1:
#endif

#define INIT_XREG                                                       \
  li x1, 0;                                                             \
  li x2, 0;                                                             \
  li x3, 0;                                                             \
  li x4, 0;                                                             \
  li x5, 0;                                                             \
  li x6, 0;                                                             \
  li x7, 0;                                                             \
  li x8, 0;                                                             \
  li x9, 0;                                                             \
  li x10, 0;                                                            \
  li x11, 0;                                                            \
  li x12, 0;                                                            \
  li x13, 0;                                                            \
  li x14, 0;                                                            \
  li x15, 0;                                                            \
  li x16, 0;                                                            \
  li x17, 0;                                                            \
  li x18, 0;                                                            \
  li x19, 0;                                                            \
  li x20, 0;                                                            \
  li x21, 0;                                                            \
  li x22, 0;                                                            \
  li x23, 0;                                                            \
  li x24, 0;                                                            \
  li x25, 0;                                                            \
  li x26, 0;                                                            \
  li x27, 0;                                                            \
  li x28, 0;                                                            \
  li x29, 0;                                                            \
  li x30, 0;                                                            \
  li x31, 0;

#define RVTEST_CODE_BEGIN                                               \
        .section .text.init;                                            \
        .align  6;                                                      \
        .globl _start;                                                  \
_start:                                                                 \
	li	sp, 0x80003ffc;	\
        /* reset vector */                                              \
        j reset_vector;                                                 \
        .align 2;                                                       \
putstr:				\
	lb	a2, 0(a1);	\
	beqz	a2, putstr_end;	\
	sb	a2, 0(a3);	\
	add	a1, a1, 1;	\
	j	putstr;		\
putstr_end:			\
	ret;			\
handle_result:			\
	li	a3, 0xA0001004;	\
	beqz	a0, handle_pass;\
handle_fail:			\
	la	a1, msg_fail;	\
	jal	putstr;		\
	srl	a0, a0, 1;	\
	addi	a2, a0, 0x30;	\
	sb	a2, 0(a3);	\
	j	handle_end;	\
handle_pass:			\
	la	a1, msg_pass;	\
	jal	putstr;		\
handle_end:			\
	li	a2, 0x0a;	\
	sb	a2, 0(a3);	\
	li	a3, 0xA0001000;	\
	li	a2, 0x01;	\
	sb	a2, 0(a3);	\
loop:				\
	j	loop;		\
reset_vector:                                                           \
        INIT_XREG;                                                      \
        li TESTNUM, 0;                                                  \
        CHECK_XLEN;                                                     \
1:

//-----------------------------------------------------------------------
// End Macro
//-----------------------------------------------------------------------

#define RVTEST_CODE_END                                                 \
        nop;

//-----------------------------------------------------------------------
// Pass/Fail Macro
//-----------------------------------------------------------------------

#define RVTEST_PASS                                                     \
        li TESTNUM, 1;                                                  \
        li a0, 0;                                                       \
        j handle_result;

#define TESTNUM gp
#define RVTEST_FAIL                                                     \
1:      beqz TESTNUM, 1b;                                               \
        sll TESTNUM, TESTNUM, 1;                                        \
        or TESTNUM, TESTNUM, 1;                                         \
        addi a0, TESTNUM, 0;                                            \
        j handle_result;

//-----------------------------------------------------------------------
// Data Section Macro
//-----------------------------------------------------------------------

#define MSG_DATA		\
msg_pass:			\
	.ascii	"PASS\0";	\
msg_fail:			\
	.ascii	"FAIL: TEST #\0";	\

#define RVTEST_DATA_BEGIN	\
	.align 4;		\
	.section .data;		\
	MSG_DATA		\
	.align 4;

#define RVTEST_DATA_END .align 4;

#endif
