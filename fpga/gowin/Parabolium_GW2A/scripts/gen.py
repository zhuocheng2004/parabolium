
v_start = '''// auto-generated
module RAM (
        input [11:0] addra,
        input clka,
        input [31:0] dina,
        output [31:0] douta,
        input ena,
        input [3:0] wea
    );

    wire [27:0] sp_dout_w[7:0];
'''

v_end = '''
endmodule
'''

v_sp = '''
    SP sp%d (
           .DO({sp_dout_w[%d],douta[%d:%d]}),
           .CLK(clka),
           .OCE(1'b1),
           .CE(1'b1),
           .RESET(1'b0),
           .WRE(ena & wea[%d]),
           .BLKSEL(3'b000),
           .AD({addra[11:0],2'b00}),
           .DI({28'b0,dina[%d:%d]})
       );

    defparam sp%d.READ_MODE = 1'b0;
    defparam sp%d.WRITE_MODE = 2'b00;
    defparam sp%d.BIT_WIDTH = 4;
    defparam sp%d.BLK_SEL = 3'b000;
    defparam sp%d.RESET_MODE = "SYNC";
'''

HEX_CHARS = '0123456789ABCDEF'

with open('data.bin', 'rb') as f:
	data = f.read()
	
if len(data) >= 0x4000:
    print(f'data size = {len(data)} >= 0x4000')
    exit(1)

remaining = len(data) & 0xff
data = data + b'\00' * (256 - remaining)

print(v_start)

for i in range(8):
	print(v_sp % (i, i, 4 * i + 3, 4 * i, i >> 1, 4 * i + 3, 4 * i, i, i, i, i, i))
	for j in range(len(data) >> 8):
		s = ''
		for k in range(64):
			v = data[256 * j + 4 * k + (i >> 1)]
			v = (v & 0xf) if (i & 1 == 0) else ((v >> 4) & 0xf)
			s = HEX_CHARS[v] + s
		print('    defparam sp%d.INIT_RAM_%c%c = 256\'h%s;' % (i, HEX_CHARS[(j >> 4) & 0xf], HEX_CHARS[j & 0xf], s))

print(v_end)
