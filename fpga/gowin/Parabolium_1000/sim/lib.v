
// ===========Oooo==========================================Oooo========
// =  Copyright (C) 2014-2024 Gowin Semiconductor Technology Co.,Ltd.
// =                     All rights reserved.
// =====================================================================
//
//  __      __      __
//  \ \    /  \    / /   [File name   ] prim_sim.v
//   \ \  / /\ \  / /    [Description ] GW2A verilog functional simulation library
//    \ \/ /  \ \/ /     [Timestamp   ] Mon October 17 11:00:30 2022
//     \  /    \  /      [version     ] 1.9.14
//      \/      \/       
//
// ===========Oooo==========================================Oooo========


`timescale 1ns / 1ps


//Block SRAM
module SP (DO, DI, BLKSEL, AD, WRE, CLK, CE, OCE, RESET);

parameter READ_MODE = 1'b0; // 1'b0: bypass mode; 1'b1: pipeline mode
parameter WRITE_MODE = 2'b00; // 2'b00: normal mode; 2'b01: write-through mode; 2'b10: read-before-write mode
parameter BIT_WIDTH = 32; // 1, 2, 4, 8, 16, 32
parameter BLK_SEL = 3'b000;
parameter RESET_MODE = "SYNC"; // SYNC, ASYNC
parameter INIT_RAM_00 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_01 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_02 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_03 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_04 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_05 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_06 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_07 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_08 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_09 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_10 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_11 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_12 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_13 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_14 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_15 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_16 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_17 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_18 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_19 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_20 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_21 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_22 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_23 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_24 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_25 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_26 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_27 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_28 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_29 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_30 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_31 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_32 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_33 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_34 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_35 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_36 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_37 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_38 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_39 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
input CLK, CE;
input OCE; // clock enable of memory output register
input RESET; // resets output registers, not memory contents
input WRE; // 1'b0: read enabled; 1'b1: write enabled
input [13:0] AD;
input [31:0] DI;
input [2:0] BLKSEL;
output [31:0] DO;

reg [31:0] pl_reg,pl_reg_async,pl_reg_sync;
reg [31:0] bp_reg,bp_reg_async,bp_reg_sync;
reg bs_en;
wire pce;
reg [16383:0] ram_MEM = {INIT_RAM_3F, INIT_RAM_3E, INIT_RAM_3D, INIT_RAM_3C,INIT_RAM_3B, INIT_RAM_3A, INIT_RAM_39, INIT_RAM_38,INIT_RAM_37, INIT_RAM_36, INIT_RAM_35, INIT_RAM_34,INIT_RAM_33, INIT_RAM_32, INIT_RAM_31, INIT_RAM_30,INIT_RAM_2F, INIT_RAM_2E, INIT_RAM_2D, INIT_RAM_2C,INIT_RAM_2B, INIT_RAM_2A, INIT_RAM_29, INIT_RAM_28,INIT_RAM_27, INIT_RAM_26, INIT_RAM_25, INIT_RAM_24,INIT_RAM_23, INIT_RAM_22, INIT_RAM_21, INIT_RAM_20,INIT_RAM_1F, INIT_RAM_1E, INIT_RAM_1D, INIT_RAM_1C,INIT_RAM_1B, INIT_RAM_1A, INIT_RAM_19, INIT_RAM_18,INIT_RAM_17, INIT_RAM_16, INIT_RAM_15, INIT_RAM_14,INIT_RAM_13, INIT_RAM_12, INIT_RAM_11, INIT_RAM_10,INIT_RAM_0F, INIT_RAM_0E, INIT_RAM_0D, INIT_RAM_0C, INIT_RAM_0B, INIT_RAM_0A, INIT_RAM_09, INIT_RAM_08,INIT_RAM_07, INIT_RAM_06, INIT_RAM_05, INIT_RAM_04,INIT_RAM_03, INIT_RAM_02, INIT_RAM_01, INIT_RAM_00};
reg [BIT_WIDTH-1:0] mem_t;
reg mc;
reg [13:0] addr;
integer dwidth = BIT_WIDTH;
integer awidth; // ADDR_WIDTH

initial begin
    bp_reg = 0;
    pl_reg = 0;
    bp_reg_async = 0;
    bp_reg_sync = 0;
    pl_reg_async = 0;
    pl_reg_sync = 0;
    mc = 1'b0;
end

initial begin
	case(dwidth)
		1: awidth = 14;
		2: awidth = 13;
		4: awidth = 12;
		8: awidth = 11;
		16: awidth = 10;
		32: awidth = 9;
		default: begin
		//	$display ("%d: Unsupported data width\n", dwidth);
		//	$finish;
		end
	endcase
end

assign DO = (READ_MODE == 1'b0)? bp_reg : pl_reg;

assign pce = CE && bs_en;   
always @ (BLKSEL)
begin
	if(BLKSEL == BLK_SEL) begin
		bs_en = 1;
	end else begin
		bs_en = 0;
	end  	
end

always@(awidth,AD,WRE,mc)begin
	if(awidth==14)begin
		addr[13:0] = AD[13:0];
		mem_t[0] =ram_MEM[addr];
	end
	else if(awidth==13)begin
		addr[13:0] = {AD[13:1],1'b0};
		mem_t[1:0] ={ram_MEM[addr+1],ram_MEM[addr]};
	end
	else if(awidth==12)begin
		addr[13:0] = {AD[13:2],2'b00};
		mem_t[3:0] ={ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]};
	end
	else if(awidth==11)begin
		addr[13:0] = {AD[13:3],3'b000};
		mem_t[7:0] ={ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]};
	end
	else if(awidth==10)begin
		addr[13:0] = {AD[13:4],4'b0000};
		mem_t[15:0] ={ram_MEM[addr+15],ram_MEM[addr+14],ram_MEM[addr+13],ram_MEM[addr+12],ram_MEM[addr+11],ram_MEM[addr+10],ram_MEM[addr+9],ram_MEM[addr+8],ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]};
	end
	else if(awidth==9)begin
		addr[13:0] = {AD[13:5],5'b00000};
		mem_t[31:0]={ram_MEM[addr+31],ram_MEM[addr+30],ram_MEM[addr+29],ram_MEM[addr+28],ram_MEM[addr+27],ram_MEM[addr+26],ram_MEM[addr+25],ram_MEM[addr+24],ram_MEM[addr+23],ram_MEM[addr+22],ram_MEM[addr+21],ram_MEM[addr+20],ram_MEM[addr+19],ram_MEM[addr+18],ram_MEM[addr+17],ram_MEM[addr+16],ram_MEM[addr+15],ram_MEM[addr+14],ram_MEM[addr+13],ram_MEM[addr+12],ram_MEM[addr+11],ram_MEM[addr+10],ram_MEM[addr+9],ram_MEM[addr+8],ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]};
	end
end

//write and read
always @(posedge CLK) begin
	if (pce) begin
    	if(WRE) begin
		    if(dwidth==1)
			    ram_MEM[addr] <= DI[0];
			else if(dwidth==2)
				{ram_MEM[addr+1],ram_MEM[addr]}<=DI[BIT_WIDTH-1:0];
			else if(dwidth==4)
				{ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]}<=DI[BIT_WIDTH-1:0];
			else if(dwidth==8)
				{ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]}<=DI[7:0];

			else if(dwidth==16) begin
				if(AD[0] == 1'b1)
					{ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]} <= DI[7:0];
				if(AD[1] == 1'b1)
					{ram_MEM[addr+15],ram_MEM[addr+14],ram_MEM[addr+13],ram_MEM[addr+12],ram_MEM[addr+11],ram_MEM[addr+10],ram_MEM[addr+9],ram_MEM[addr+8]} <= DI[15:8];
			end
			else if(dwidth==32) begin
				if(AD[0] == 1'b1)
					{ram_MEM[addr+7],ram_MEM[addr+6],ram_MEM[addr+5],ram_MEM[addr+4],ram_MEM[addr+3],ram_MEM[addr+2],ram_MEM[addr+1],ram_MEM[addr]}<=DI[7:0];
				if(AD[1] == 1'b1)
					{ram_MEM[addr+15],ram_MEM[addr+14],ram_MEM[addr+13],ram_MEM[addr+12],ram_MEM[addr+11],ram_MEM[addr+10],ram_MEM[addr+9],ram_MEM[addr+8]}<=DI[15:8];
				if(AD[2] == 1'b1)
					{ram_MEM[addr+23],ram_MEM[addr+22],ram_MEM[addr+21],ram_MEM[addr+20],ram_MEM[addr+19],ram_MEM[addr+18],ram_MEM[addr+17],ram_MEM[addr+16]} <= DI[23:16];	
				if(AD[3] == 1'b1)
					{ram_MEM[addr+31],ram_MEM[addr+30],ram_MEM[addr+29],ram_MEM[addr+28],ram_MEM[addr+27],ram_MEM[addr+26],ram_MEM[addr+25],ram_MEM[addr+24]} <= DI[31:24];	
			end
		    mc <= ~mc;
        end
	end
end	

always @ (bp_reg_async or bp_reg_sync or pl_reg_async or pl_reg_sync) begin
    if(RESET_MODE == "ASYNC") begin
        bp_reg <= bp_reg_async;
        pl_reg <= pl_reg_async;
    end
    else begin
        bp_reg <= bp_reg_sync;
        pl_reg <= pl_reg_sync;
    end
end

always @(posedge CLK or posedge RESET) begin
	if (RESET) begin
		bp_reg_async <= 0;
	end else begin
		if (pce) begin
    	    if(WRE) begin	
				if (WRITE_MODE == 2'b01) begin
					bp_reg_async[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
                    if(dwidth <= 8) begin
					    bp_reg_async[BIT_WIDTH-1:0] <= DI[BIT_WIDTH-1:0];
                    end else if(dwidth==16) begin
						if(AD[0] == 1'b1)
							bp_reg_async[7:0] <= DI[7:0];
						if(AD[1] == 1'b1)
                            bp_reg_async[15:8] <= DI[15:8];
				    end else if(dwidth==32) begin
						if(AD[0] == 1'b1)
                            bp_reg_async[7:0]  <= DI[7:0];
						if(AD[1] == 1'b1)
                            bp_reg_async[15:8] <= DI[15:8];
						if(AD[2] == 1'b1)
                            bp_reg_async[23:16] <= DI[23:16];	
						if(AD[3] == 1'b1)
                            bp_reg_async[31:24] <= DI[31:24];
			        end
				end

				if (WRITE_MODE == 2'b10) begin
					bp_reg_async[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
				end
				
			end else begin // WRE==0, read
				bp_reg_async[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
			end
		end
	end
end	

always @(posedge CLK) begin
	if (RESET) begin
		bp_reg_sync <= 0;
	end else begin
		if (pce) begin
    	    if(WRE) begin	
				if (WRITE_MODE == 2'b01) begin
					bp_reg_sync[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
                    if(dwidth <= 8) begin
					    bp_reg_sync[BIT_WIDTH-1:0] <= DI[BIT_WIDTH-1:0];                       
                    end else if(dwidth==16) begin
						if(AD[0] == 1'b1)
							bp_reg_sync[7:0] <= DI[7:0];
						if(AD[1] == 1'b1)
                            bp_reg_sync[15:8] <= DI[15:8];                        
				    end else if(dwidth==32) begin
						if(AD[0] == 1'b1)
                            bp_reg_sync[7:0]  <= DI[7:0];
						if(AD[1] == 1'b1)
                            bp_reg_sync[15:8] <= DI[15:8];
						if(AD[2] == 1'b1)
                            bp_reg_sync[23:16] <= DI[23:16];	
						if(AD[3] == 1'b1)
                            bp_reg_sync[31:24] <= DI[31:24];
			        end
				end

				if (WRITE_MODE == 2'b10) begin
					bp_reg_sync[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
				end
				
			end else begin // WRE==0, read
				bp_reg_sync[BIT_WIDTH-1:0] <= mem_t[BIT_WIDTH-1:0];
			end
		end
	end
end	

always @(posedge CLK or posedge RESET) begin
	if (RESET) begin
		pl_reg_async <= 0;
	end else begin
		if(OCE) begin
			pl_reg_async <= bp_reg;
		end
	end
end	

always @(posedge CLK) begin
	if (RESET) begin
		pl_reg_sync <= 0;
	end else begin
		if(OCE) begin
			pl_reg_sync <= bp_reg;
		end
	end
end	

endmodule  // SP: single port 16k Block SRAM



//SDPB
module SDPB (DO, DI, BLKSELA, BLKSELB, ADA, ADB, CLKA, CLKB, CEA, CEB, OCE, RESETA, RESETB);

parameter READ_MODE = 1'b0; // 1'b0: bypass mode; 1'b1: pipeline mode
parameter BIT_WIDTH_0 = 32; // 1, 2, 4, 8, 16, 32
parameter BIT_WIDTH_1 = 32; // 1, 2, 4, 8, 16, 32
parameter BLK_SEL_0 = 3'b000;
parameter BLK_SEL_1 = 3'b000;
parameter RESET_MODE = "SYNC"; //SYNC,ASYNC
parameter INIT_RAM_00 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_01 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_02 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_03 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_04 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_05 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_06 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_07 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_08 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_09 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_0F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_10 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_11 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_12 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_13 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_14 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_15 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_16 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_17 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_18 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_19 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_1F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_20 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_21 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_22 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_23 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_24 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_25 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_26 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_27 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_28 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_29 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_2F = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_30 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_31 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_32 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_33 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_34 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_35 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_36 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_37 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_38 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_39 = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3A = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3B = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3C = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3D = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3E = 256'h0000000000000000000000000000000000000000000000000000000000000000;
parameter INIT_RAM_3F = 256'h0000000000000000000000000000000000000000000000000000000000000000;

input CLKA, CEA, CLKB, CEB;
input OCE; // clock enable of memory output register
input RESETA, RESETB; // resets output registers, not memory contents
input [13:0] ADA, ADB;
input [31:0] DI;
input [2:0] BLKSELA, BLKSELB;
output [31:0] DO;

reg [31:0] pl_reg,pl_reg_async,pl_reg_sync;
reg [31:0] bp_reg,bp_reg_async,bp_reg_sync;
reg [16383:0] ram_MEM ={INIT_RAM_3F, INIT_RAM_3E, INIT_RAM_3D, INIT_RAM_3C,INIT_RAM_3B, INIT_RAM_3A, INIT_RAM_39, INIT_RAM_38,INIT_RAM_37, INIT_RAM_36, INIT_RAM_35, INIT_RAM_34,INIT_RAM_33, INIT_RAM_32, INIT_RAM_31, INIT_RAM_30,INIT_RAM_2F, INIT_RAM_2E, INIT_RAM_2D, INIT_RAM_2C,INIT_RAM_2B, INIT_RAM_2A, INIT_RAM_29, INIT_RAM_28,INIT_RAM_27, INIT_RAM_26, INIT_RAM_25, INIT_RAM_24,INIT_RAM_23, INIT_RAM_22, INIT_RAM_21, INIT_RAM_20,INIT_RAM_1F, INIT_RAM_1E, INIT_RAM_1D, INIT_RAM_1C,INIT_RAM_1B, INIT_RAM_1A, INIT_RAM_19, INIT_RAM_18,INIT_RAM_17, INIT_RAM_16, INIT_RAM_15, INIT_RAM_14,INIT_RAM_13, INIT_RAM_12, INIT_RAM_11, INIT_RAM_10,INIT_RAM_0F, INIT_RAM_0E, INIT_RAM_0D, INIT_RAM_0C, INIT_RAM_0B, INIT_RAM_0A, INIT_RAM_09, INIT_RAM_08,INIT_RAM_07, INIT_RAM_06, INIT_RAM_05, INIT_RAM_04,INIT_RAM_03, INIT_RAM_02, INIT_RAM_01, INIT_RAM_00} ;
reg [BIT_WIDTH_0-1:0] mem_a;
reg [BIT_WIDTH_1-1:0] mem_b;
reg [13:0] addr_a, addr_b;
reg mc,bs_ena,bs_enb;
wire pcea;
wire pceb;

integer bit_width_d0 = BIT_WIDTH_0;
integer bit_width_d1 = BIT_WIDTH_1;
integer bit_width_a0, bit_width_a1; // ADDR_WIDTH

initial begin
    bp_reg = 0;
    pl_reg = 0;
    bp_reg_async = 0;
    bp_reg_sync = 0;
    pl_reg_async = 0;
    pl_reg_sync = 0;
    mc = 1'b0;
end

initial begin
	case(bit_width_d0)
		1: bit_width_a0 = 14;
		2: bit_width_a0 = 13;
		4: bit_width_a0 = 12;
		8: bit_width_a0 = 11;
		16: bit_width_a0 = 10;
		32: bit_width_a0 = 9;
		default: begin
		//	$display ("%d: Unsupported data width\n", bit_width_d0);
		//	$finish;
		end
	endcase
	case(bit_width_d1)
		1: bit_width_a1 = 14;
		2: bit_width_a1 = 13;
		4: bit_width_a1 = 12;
		8: bit_width_a1 = 11;
		16: bit_width_a1 = 10;
		32: bit_width_a1 = 9;
		default: begin
		//	$display ("%d: Unsupported data width\n", bit_width_d1);
		//	$finish;
		end
	endcase
end

assign DO = (READ_MODE == 1'b0)? bp_reg: pl_reg;

assign pcea = CEA && bs_ena;   
assign pceb = CEB && bs_enb;

always @ (BLKSELA, BLKSELB)
begin
	if(BLKSELA == BLK_SEL_0) begin
		bs_ena = 1;
	end else begin
		bs_ena = 0;
	end

    if(BLKSELB == BLK_SEL_1) begin
		bs_enb = 1;
	end else begin
		bs_enb = 0;
	end
end

always@(ADA,ADB,bit_width_a0,bit_width_a1,mc)begin
	if(bit_width_a0==14)begin
		addr_a[13:0] = ADA[13:0];
		mem_a[0] = ram_MEM[addr_a];
	end
	else if(bit_width_a0==13)begin
		addr_a[13:0] = {ADA[13:1],1'b0};
		mem_a[1:0] = {ram_MEM[addr_a+1],ram_MEM[addr_a]};
	end
	else if(bit_width_a0==12)begin
		addr_a[13:0] = {ADA[13:2],2'b00};
		mem_a[3:0] = {ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]};
	end
	else if(bit_width_a0==11)begin
		addr_a[13:0] = {ADA[13:3],3'b000};
		mem_a[7:0] = {ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]};
	end
	else if(bit_width_a0==10)begin
		addr_a[13:0] = {ADA[13:4],4'b0000};
		mem_a[15:0] = {ram_MEM[addr_a+15],ram_MEM[addr_a+14],ram_MEM[addr_a+13],ram_MEM[addr_a+12],ram_MEM[addr_a+11],ram_MEM[addr_a+10],ram_MEM[addr_a+9],ram_MEM[addr_a+8],ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]};
	end
	else if(bit_width_a0==9)begin
		addr_a[13:0] = {ADA[13:5],5'b00000};
		mem_a[31:0] = {ram_MEM[addr_a+31],ram_MEM[addr_a+30],ram_MEM[addr_a+29],ram_MEM[addr_a+28],ram_MEM[addr_a+27],ram_MEM[addr_a+26],ram_MEM[addr_a+25],ram_MEM[addr_a+24],ram_MEM[addr_a+23],ram_MEM[addr_a+22],ram_MEM[addr_a+21],ram_MEM[addr_a+20],ram_MEM[addr_a+19],ram_MEM[addr_a+18],ram_MEM[addr_a+17],ram_MEM[addr_a+16],ram_MEM[addr_a+15],ram_MEM[addr_a+14],ram_MEM[addr_a+13],ram_MEM[addr_a+12],ram_MEM[addr_a+11],ram_MEM[addr_a+10],ram_MEM[addr_a+9],ram_MEM[addr_a+8],ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]};
	end
	if(bit_width_a1==14)begin
		addr_b[13:0] = ADB[13:0];
		mem_b[0]=ram_MEM[addr_b];
	end
	else if(bit_width_a1==13)begin
		addr_b[13:0] = {ADB[13:1],1'b0};
		mem_b[1:0]={ram_MEM[addr_b+1],ram_MEM[addr_b]};
	end
	else if(bit_width_a1==12)begin
		addr_b[13:0] = {ADB[13:2],2'b00};
		mem_b[3:0]={ram_MEM[addr_b+3],ram_MEM[addr_b+2],ram_MEM[addr_b+1],ram_MEM[addr_b]};
	end
	else if(bit_width_a1==11)begin
		addr_b[13:0] = {ADB[13:3],3'b000};
		mem_b[7:0]={ram_MEM[addr_b+7],ram_MEM[addr_b+6],ram_MEM[addr_b+5],ram_MEM[addr_b+4],ram_MEM[addr_b+3],ram_MEM[addr_b+2],ram_MEM[addr_b+1],ram_MEM[addr_b]};
	end
	else if(bit_width_a1==10)begin
		addr_b[13:0] = {ADB[13:4],4'b0000};
		mem_b[15:0]={ram_MEM[addr_b+15],ram_MEM[addr_b+14],ram_MEM[addr_b+13],ram_MEM[addr_b+12],ram_MEM[addr_b+11],ram_MEM[addr_b+10],ram_MEM[addr_b+9],ram_MEM[addr_b+8],ram_MEM[addr_b+7],ram_MEM[addr_b+6],ram_MEM[addr_b+5],ram_MEM[addr_b+4],ram_MEM[addr_b+3],ram_MEM[addr_b+2],ram_MEM[addr_b+1],ram_MEM[addr_b]};

	end
	else if(bit_width_a1==9)begin
		addr_b[13:0] = {ADB[13:5],5'b00000};
		mem_b[31:0]={ ram_MEM[addr_b+31],ram_MEM[addr_b+30],ram_MEM[addr_b+29],ram_MEM[addr_b+28],ram_MEM[addr_b+27],ram_MEM[addr_b+26],ram_MEM[addr_b+25],ram_MEM[addr_b+24],ram_MEM[addr_b+23],ram_MEM[addr_b+22],ram_MEM[addr_b+21],ram_MEM[addr_b+20],ram_MEM[addr_b+19],ram_MEM[addr_b+18],ram_MEM[addr_b+17],ram_MEM[addr_b+16],ram_MEM[addr_b+15],ram_MEM[addr_b+14],ram_MEM[addr_b+13],ram_MEM[addr_b+12],ram_MEM[addr_b+11],ram_MEM[addr_b+10],ram_MEM[addr_b+9],ram_MEM[addr_b+8],ram_MEM[addr_b+7],ram_MEM[addr_b+6],ram_MEM[addr_b+5],ram_MEM[addr_b+4],ram_MEM[addr_b+3],ram_MEM[addr_b+2],ram_MEM[addr_b+1],ram_MEM[addr_b]};

	end
end

always @(posedge CLKA) begin
	if (pcea) begin
	    if(bit_width_d0==1)
			ram_MEM[addr_a] <= DI[0];
		else if(bit_width_d0==2)
			{ram_MEM[addr_a+1],ram_MEM[addr_a]}<=DI[BIT_WIDTH_0-1:0];
		else if(bit_width_d0==4)
			{ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]}<=DI[BIT_WIDTH_0-1:0];
		else if(bit_width_d0==8)
			{ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]}<=DI[BIT_WIDTH_0-1:0];
		else if(bit_width_d0==16) begin
			if(ADA[0] == 1'b1)
				{ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]}<=DI[7:0];
			if(ADA[1] ==1'b1)
				{ram_MEM[addr_a+15],ram_MEM[addr_a+14],ram_MEM[addr_a+13],ram_MEM[addr_a+12],ram_MEM[addr_a+11],ram_MEM[addr_a+10],ram_MEM[addr_a+9],ram_MEM[addr_a+8]}<=DI[15:8];
		end
		else if(bit_width_d0==32) begin
			if(ADA[0] == 1'b1)
				{ram_MEM[addr_a+7],ram_MEM[addr_a+6],ram_MEM[addr_a+5],ram_MEM[addr_a+4],ram_MEM[addr_a+3],ram_MEM[addr_a+2],ram_MEM[addr_a+1],ram_MEM[addr_a]}<=DI[7:0];
			if(ADA[1] == 1'b1)
				{ram_MEM[addr_a+15],ram_MEM[addr_a+14],ram_MEM[addr_a+13],ram_MEM[addr_a+12],ram_MEM[addr_a+11],ram_MEM[addr_a+10],ram_MEM[addr_a+9],ram_MEM[addr_a+8]}<=DI[15:8];
			if(ADA[2] == 1'b1)
				{ram_MEM[addr_a+23],ram_MEM[addr_a+22],ram_MEM[addr_a+21],ram_MEM[addr_a+20],ram_MEM[addr_a+19],ram_MEM[addr_a+18],ram_MEM[addr_a+17],ram_MEM[addr_a+16]} <=DI[23:16];
			if(ADA[3] == 1'b1)
				{ram_MEM[addr_a+31],ram_MEM[addr_a+30],ram_MEM[addr_a+29],ram_MEM[addr_a+28],ram_MEM[addr_a+27],ram_MEM[addr_a+26],ram_MEM[addr_a+25],ram_MEM[addr_a+24]} <=DI[31:24];
		end
		mc <= ~mc;
	end
end

always @ (bp_reg_async or bp_reg_sync or pl_reg_async or pl_reg_sync) begin
    if(RESET_MODE == "ASYNC") begin
        bp_reg <= bp_reg_async;
        pl_reg <= pl_reg_async;
    end
    else begin
        bp_reg <= bp_reg_sync;
        pl_reg <= pl_reg_sync;
    end
end

always @(posedge CLKB or posedge RESETB) begin
	if (RESETB) begin
		pl_reg_async <= 0;
		bp_reg_async <= 0;
	end else begin
		if(OCE) begin
			pl_reg_async <= bp_reg;
		end
		if (pceb) begin
			bp_reg_async[BIT_WIDTH_1-1:0] <= mem_b[BIT_WIDTH_1-1:0];
		end
	end
end

always @(posedge CLKB) begin
	if (RESETB) begin
		pl_reg_sync <= 0;
		bp_reg_sync <= 0;
	end else begin
		if(OCE) begin
			pl_reg_sync <= bp_reg;
		end
		if (pceb) begin
			bp_reg_sync[BIT_WIDTH_1-1:0] <= mem_b[BIT_WIDTH_1-1:0];
		end
	end
end

endmodule // SDPB: Semi dual port 16k Block SRAM



//rPLL, revision PLL
module rPLL (CLKOUT, CLKOUTP, CLKOUTD, CLKOUTD3, LOCK, CLKIN, CLKFB, FBDSEL, IDSEL, ODSEL, DUTYDA, PSDA, FDLY, RESET, RESET_P);
input CLKIN;
input CLKFB;
input RESET; 
input RESET_P; 
input [5:0] FBDSEL; 
input [5:0] IDSEL;
input [5:0] ODSEL;
input [3:0] PSDA,FDLY; 
input [3:0] DUTYDA;

output CLKOUT;
output LOCK;
output CLKOUTP;
output CLKOUTD;
output CLKOUTD3;

parameter FCLKIN = "100.0"; // frequency of the CLKIN(M)
parameter DYN_IDIV_SEL= "false";//true:IDSEL; false:IDIV_SEL
parameter IDIV_SEL = 0; // 0:1,1:2...63:64. 1~64
parameter DYN_FBDIV_SEL= "false";//true:FBDSEL; false:FBDIV_SEL
parameter FBDIV_SEL = 0; // 0:1,1:2...63:64. 1~64
parameter DYN_ODIV_SEL= "false";//true:ODSEL; false:ODIV_SEL
parameter ODIV_SEL = 8; // 2/4/8/16/32/48/64/80/96/112/128

parameter PSDA_SEL= "0000";//
parameter DYN_DA_EN = "false";//true:PSDA or DUTYDA or FDA; false: DA_SEL
parameter DUTYDA_SEL= "1000";//

parameter CLKOUT_FT_DIR = 1'b1; // CLKOUT fine tuning direction. 1'b1 only
parameter CLKOUTP_FT_DIR = 1'b1; // 1'b1 only
parameter CLKOUT_DLY_STEP = 0; // 0,1,2,4
parameter CLKOUTP_DLY_STEP = 0; // 0,1,2

parameter CLKFB_SEL = "internal"; //"internal", "external";
parameter CLKOUT_BYPASS = "false";  //"true"; "false"
parameter CLKOUTP_BYPASS = "false";   //"true"; "false"
parameter CLKOUTD_BYPASS = "false";  //"true"; "false"
parameter DYN_SDIV_SEL = 2; // 2~128,only even num
parameter CLKOUTD_SRC =  "CLKOUT";  //CLKOUT,CLKOUTP
parameter CLKOUTD3_SRC = "CLKOUT"; //CLKOUT,CLKOUTP
parameter DEVICE = "GW2A-55";//"GW2A-18","GW2A-55","GW2AR-18","GW2A-55C","GW2A-18C","GW2AR-18C","GW2ANR-18C","GW2AN-55C"

wire resetn;
wire [5:0] IDIV_SEL_reg,FBDIV_SEL_reg;
wire [5:0] IDIV_dyn,FBDIV_dyn;
reg [5:0] IDIV_SEL_reg1,FBDIV_SEL_reg1,ODSEL_reg;
wire div_dyn_change;
integer IDIV_reg,FBDIV_reg;
wire clk_div_src;
reg clk_effect,oclk_effect,oclk_build;
realtime curtime,pretime,fb_delay;
realtime clkin_cycle[4:0];
realtime clkin_period,clkin_period1,clkout_period,tclkout_half,tclkout_half_new;
realtime clkfb_curtime,clkin_curtime,FB_dly,FB_dly0;
reg clkin_init,fb_clk_init;
reg clkout,clk_out,clkfb_reg,clkoutp,clk_ps_reg,clk_ps_reg0;
reg clkfb;
reg lock_reg;
realtime ps_dly,f_dly,clkout_duty, ps_value, duty_value,tclkp_duty;
real unit_div=1.0, real_fbdiv=1.0;
integer cnt_div;
reg clkout_div_reg;
integer multi_clkin;
wire div3_in;
integer cnt_div3;
reg div3_reg;
reg clkfb_init,div3_init,pre_div3_in;


initial begin
IDIV_reg = 1;
FBDIV_reg = 1;
clkin_cycle[0] = 0;
clkin_cycle[1] = 0;
clkin_cycle[2] = 0;
clkin_cycle[3] = 0;
clkin_cycle[4] = 0;
clkin_period = 0;
clkin_period1 = 0;
clkout_period = 0;
clk_effect = 1'b0;
oclk_effect = 1'b0;
oclk_build = 1'b0;
clkfb_reg = 1'b0;
clkout = 1'b0;
clk_out = 1'b0;
clkfb = 1'b0;
clkoutp = 1'b0;
clkin_init = 1'b1;
fb_clk_init = 1'b1;
clkfb_init = 1'b1;
FB_dly = 0.0;
FB_dly0 = 0.0;
clkin_curtime = 0.0;
clkfb_curtime = 0.0;
lock_reg = 0;
clk_ps_reg=0;
clk_ps_reg0=0;
clkout_div_reg=0;
cnt_div=0;
div3_init = 1'b1;
cnt_div3=0;
div3_reg=0;
f_dly = 0.0;
ps_dly = 0.0;
////////////
end

assign resetn = ~( RESET | RESET_P );

// determine period of CLKIN and clkout
always @(posedge CLKIN or negedge resetn) begin
    if(!resetn) begin
        clk_effect <= 1'b0;
        clkin_cycle[0] <= 0;
    end else begin
        pretime <= curtime;
        curtime <= $realtime;

        if(pretime>0) begin
	        clkin_cycle[0] <= curtime -  pretime;
        end

        if(clkin_cycle[0] > 0) begin
            clkin_cycle[1] <= clkin_cycle[0];
	        clkin_cycle[2] <= clkin_cycle[1];
	        clkin_cycle[3] <= clkin_cycle[2];
            clkin_cycle[4] <= clkin_cycle[3];
        end
    
        if (clkin_cycle[0] > 0) begin
            if(((clkin_cycle[0] - clkin_period1 < 0.01) && (clkin_cycle[0] - clkin_period1 > -0.01)) &&(!div_dyn_change)) begin
                clk_effect <= 1'b1;
                clkin_period <= clkin_period1;
            end else begin
                clk_effect <= 1'b0;
            end
        end
    end
end

always @(clkin_cycle[0] or clkin_cycle[1] or clkin_cycle[2] or clkin_cycle[3] or clkin_cycle[4]  or clkin_period1) begin
    if(clkin_cycle[0]!=clkin_period1) begin
		clkin_period1 <= (clkin_cycle[0]+clkin_cycle[1]+clkin_cycle[2]+clkin_cycle[3]+clkin_cycle[4])/5;
    end
end

/*IDSEL/FBDSEL    IDIV_dyn/FBDIV_dyn
111111	divider   /1
111110	divider   /2
.	.
.	.
.	.
000000	divider   /64
*/
assign IDIV_dyn = 64 - IDSEL;
assign FBDIV_dyn = 64 - FBDSEL;

assign IDIV_SEL_reg = (DYN_IDIV_SEL == "true") ? IDIV_dyn : (IDIV_SEL+1) ;
assign FBDIV_SEL_reg = (DYN_FBDIV_SEL == "true") ? FBDIV_dyn : (FBDIV_SEL+1) ;

always @(posedge CLKIN) begin
    IDIV_SEL_reg1 <= IDIV_SEL_reg;
    FBDIV_SEL_reg1 <= FBDIV_SEL_reg;
    ODSEL_reg <= ODSEL;
end

assign div_dyn_change = (IDIV_SEL_reg1 != IDIV_SEL_reg) || (FBDIV_SEL_reg1 != FBDIV_SEL_reg) || (ODSEL_reg != ODSEL);

always @(clkin_period or IDIV_SEL_reg or FBDIV_SEL_reg) begin
    real_fbdiv = (FBDIV_SEL_reg * unit_div);
    clkout_period = ((clkin_period * IDIV_SEL_reg) / real_fbdiv);
    tclkout_half = (clkout_period / 2);
end

realtime clk_tlock_cur;
realtime max_tlock;
integer cnt_lock;
initial begin
    clk_tlock_cur = 0.0;
    max_tlock = 0.0;
    cnt_lock = 0;
end

// lock time
always @(posedge CLKIN or negedge resetn) begin
    if (resetn == 1'b0) begin
        max_tlock <= 0.0;
    end else begin
        if((clkin_cycle[0] >= 2) && (clkin_cycle[0] <= 40)) begin
            max_tlock <= 50000;
        end else if ((clkin_cycle[0] > 40) && (clkin_cycle[0] <= 500)) begin
            max_tlock <= 200000;
        end
    end
end

always @(posedge CLKIN or negedge resetn) begin
    if (resetn == 1'b0) begin
        lock_reg <= 1'b0;
        oclk_effect <= 1'b0;
    end else begin
        if(clk_effect == 1'b1) begin
            cnt_lock <= cnt_lock + 1;

            if(cnt_lock > ((max_tlock/clkin_period) - 10)) begin
                oclk_effect <= 1'b1;
            end else begin
                oclk_effect <= 1'b0;
            end

            if(cnt_lock > (max_tlock/clkin_period)) begin
                lock_reg <= 1'b1;
            end else begin
                lock_reg <= 1'b0;
            end
        end else begin
            oclk_effect <= 1'b0;
            cnt_lock <= 0;
            lock_reg <= 1'b0;
        end
    end
end

// calculate CLKFB feedback delay
always @(posedge CLKIN) begin
    if (clkin_init == 1'b1) begin
        clkin_curtime=$realtime;
        clkin_init = 1'b0;
    end
end

always @(posedge CLKFB) begin
    if (fb_clk_init == 1'b1) begin
        clkfb_curtime=$realtime;
        fb_clk_init = 1'b0;
    end
end

always @(CLKFB or CLKIN) begin
    if ((clkfb_curtime > 0) && (clkin_curtime > 0)) begin
        FB_dly0 = clkfb_curtime - clkin_curtime;
        if ((FB_dly0 >= 0) && (clkin_cycle[0] > 0)) begin
            multi_clkin = FB_dly0 / (clkin_cycle[0]);
            FB_dly = clkin_cycle[0] - (FB_dly0 - (clkin_cycle[0]) * multi_clkin);
        end
    end
end

// clkout
always @(clkfb_reg or oclk_effect) begin
    if(oclk_effect == 1'b0) begin
        clkfb_reg = 1'b0;
    end
    else begin
        if(clkfb_init == 1'b1) begin
            clkfb_reg <= 1'b1;
            clkfb_init = 1'b0;
        end
        else begin
            clkfb_reg <= #tclkout_half ~clkfb_reg;
        end
    end
end

always @(clkfb_reg) begin
    if (CLKFB_SEL == "internal") begin
        clkfb <= clkfb_reg;
    end else begin
        clkfb <= #(FB_dly) clkfb_reg;
    end
end

always @(posedge clkfb) begin
    clkout <= 1'b1;
    #tclkout_half_new
    clkout <= 1'b0;
end

always @(CLKIN or oclk_effect or clkout or resetn) begin
    if (resetn == 1'b0) begin
        clk_out <= 1'b0;
    end
    //else if (oclk_effect == 1'b1) begin
    else begin
        clk_out <= clkout;
    end
end

assign CLKOUT = (CLKOUT_BYPASS == "true")? CLKIN : clk_out;
assign LOCK = lock_reg;  

//clkout_p
// DYN_DA_EN == "false".
// phase_shift_value
always @(*) begin
    case (PSDA_SEL)
	    "0000": ps_value = (clkout_period *  0)/16;
	    "0001": ps_value = (clkout_period *  1)/16;
	    "0010": ps_value = (clkout_period *  2)/16;
	    "0011": ps_value = (clkout_period *  3)/16;
	    "0100": ps_value = (clkout_period *  4)/16;
	    "0101": ps_value = (clkout_period *  5)/16;
	    "0110": ps_value = (clkout_period *  6)/16;
	    "0111": ps_value = (clkout_period *  7)/16;
	    "1000": ps_value = (clkout_period *  8)/16;
	    "1001": ps_value = (clkout_period *  9)/16;
	    "1010": ps_value = (clkout_period * 10)/16;
	    "1011": ps_value = (clkout_period * 11)/16;
	    "1100": ps_value = (clkout_period * 12)/16;
	    "1101": ps_value = (clkout_period * 13)/16;
	    "1110": ps_value = (clkout_period * 14)/16;
	    "1111": ps_value = (clkout_period * 15)/16;
	endcase
end

always @(*) begin
	case (DUTYDA_SEL)
	    "0000": duty_value = (clkout_period *  0)/16;
	    "0001": duty_value = (clkout_period *  1)/16;
	    "0010": duty_value = (clkout_period *  2)/16;
	    "0011": duty_value = (clkout_period *  3)/16;
	    "0100": duty_value = (clkout_period *  4)/16;
	    "0101": duty_value = (clkout_period *  5)/16;
	    "0110": duty_value = (clkout_period *  6)/16;
	    "0111": duty_value = (clkout_period *  7)/16;
	    "1000": duty_value = (clkout_period *  8)/16;
	    "1001": duty_value = (clkout_period *  9)/16;
	    "1010": duty_value = (clkout_period * 10)/16;
	    "1011": duty_value = (clkout_period * 11)/16;
	    "1100": duty_value = (clkout_period * 12)/16;
	    "1101": duty_value = (clkout_period * 13)/16;
	    "1110": duty_value = (clkout_period * 14)/16;
	    "1111": duty_value = (clkout_period * 15)/16;
	endcase
end

// DYN_DA_EN == "true".
always @(FDLY) begin
    if(DYN_DA_EN == "true") begin
        if(DEVICE == "GW1N-1" || DEVICE == "GW1N-1S")begin
            case(FDLY)
                4'b0000  : f_dly = 0.000;
                4'b0001  : f_dly = 0.125;
                4'b0010  : f_dly = 0.250;
                4'b0100  : f_dly = 0.500;
                4'b1000  : f_dly = 1.000;
                default : f_dly = 0.000;
            endcase
        end else begin
            case(FDLY)
                4'b1111  : f_dly = 0.000;
                4'b1110  : f_dly = 0.125;
                4'b1101  : f_dly = 0.250;
                4'b1011  : f_dly = 0.500;
                4'b0111  : f_dly = 1.000;
                default : f_dly = 0.000;
            endcase
        end
    end
end

always @ (PSDA or DUTYDA or ps_value or duty_value) begin
    if (DYN_DA_EN == "true") begin
        ps_dly = (clkout_period *PSDA)/16;
        if (DUTYDA > PSDA) begin
            clkout_duty = (clkout_period * (DUTYDA - PSDA))/16;
        end else if (DUTYDA < PSDA) begin
            clkout_duty = (clkout_period*(16 + DUTYDA - PSDA))/16;
        end else begin
            clkout_duty = (clkout_period)/2;
        end
    end else begin
        ps_dly= ps_value;
        clkout_duty = duty_value;
    end
end

always @(tclkout_half or clkout_duty) begin
    if (DYN_DA_EN == "false") begin
        tclkout_half_new <= tclkout_half;
        tclkp_duty <= clkout_duty;
    end else begin
        if (CLKOUT_FT_DIR == 1'b1) begin
            tclkout_half_new <= tclkout_half - (0.05 * CLKOUT_DLY_STEP);
        end else begin
            tclkout_half_new <= tclkout_half + (0.05 * CLKOUT_DLY_STEP);
        end

        if (CLKOUTP_FT_DIR == 1'b1) begin
            tclkp_duty <= clkout_duty - (0.05 * CLKOUTP_DLY_STEP);
	    end else begin
            tclkp_duty <= clkout_duty + (0.05 * CLKOUTP_DLY_STEP);
        end
	end
end

always @(posedge clkfb) begin
    clkoutp <= 1'b1;
    #tclkp_duty
    clkoutp <= 1'b0;
end

always @(clkoutp) begin
    clk_ps_reg0 <= #(ps_dly+f_dly) clkoutp;    
end
      
always @(CLKIN or oclk_effect or clk_ps_reg0 or resetn) begin
    if (resetn == 1'b0) begin
        clk_ps_reg <= 1'b0;
    end 
    //else if (oclk_effect == 1'b1) begin
    else begin
        clk_ps_reg <= clk_ps_reg0;
    end
end

assign CLKOUTP = (CLKOUTP_BYPASS == "true")? CLKIN : clk_ps_reg;

//divide
assign clk_div_src = (CLKOUTD_SRC=="CLKOUTP") ? clk_ps_reg0:clkout;

always @(posedge clk_div_src or negedge resetn) begin
    if (!resetn) begin
        cnt_div <= 0;
	    clkout_div_reg <= 0;
    end else begin
        cnt_div = cnt_div + 1;
		if (cnt_div == DYN_SDIV_SEL/2) begin
	        clkout_div_reg <= ~clkout_div_reg;
			cnt_div <= 0;
        end
	end
end    
    
assign CLKOUTD = (CLKOUTD_BYPASS == "true") ? CLKIN : clkout_div_reg;

// div3
assign div3_in=(CLKOUTD3_SRC=="CLKOUTP")?clk_ps_reg:clk_out; 

always @ (div3_in) begin
    pre_div3_in <= div3_in;
end

always @(div3_in or negedge resetn) begin
    if(div3_init == 1'b1) begin
        if(pre_div3_in == 1'b1 && div3_in == 1'b0) begin
	        div3_reg <= 1;
            div3_init = 1'b0;
            cnt_div3 = 0;
        end
    end else if(resetn == 1'b0) begin
         div3_reg <= 0;
         cnt_div3 = 0;
    end else begin
        cnt_div3 = cnt_div3+1;
        if(cnt_div3 == 3) begin
            div3_reg <= ~div3_reg;
            cnt_div3 = 0;
        end
    end
end

assign CLKOUTD3 = div3_reg;

endmodule
