
import sys

v_start = '''// auto-generated
module RAM (
        input           clk,

        input           ren,
        input [11:0]    raddr,
        output [31:0]   rdata,

        input           wen,
        input [11:0]    waddr,
        input [31:0]    wdata,
        input [3:0]     wmask
    );

    wire [27:0] sp_dout_w[7:0];
'''

v_end = '''
endmodule
'''

v_sp = '''
    SDPB sdpb%d (
            .CLKA(clk),
            .CLKB(clk),
            .RESETA(1'b0),
            .RESETB(1'b0),
            .CEA(wen & wmask[%d]),
            .CEB(ren),
            .OCE(1'b1),
            .BLKSELA(3'b000),
            .BLKSELB(3'b000),
            .ADA({ waddr[11:0], 2'b00 }),
            .ADB({ raddr[11:0], 2'b00 }),
            .DO({sp_dout_w[%d], rdata[%d:%d] }),
            .DI({ 28'h0000000, wdata[%d:%d] })
        );

    defparam sdpb%d.READ_MODE = 1'b0;
    defparam sdpb%d.BIT_WIDTH_0 = 4;
    defparam sdpb%d.BIT_WIDTH_1 = 4;
    defparam sdpb%d.BLK_SEL_0 = 3'b000;
    defparam sdpb%d.BLK_SEL_1 = 3'b000;
    defparam sdpb%d.RESET_MODE = "SYNC";
'''

HEX_CHARS = '0123456789ABCDEF'

if len(sys.argv) < 2:
    print(f"Usage: {sys.argv[0]} filename")
    sys.exit(1)

with open(sys.argv[1], 'rb') as f:
    data = f.read()
    
if len(data) >= 0x4000:
    print(f'data size = {len(data)} >= 0x4000')
    exit(1)

remaining = len(data) & 0xff
data = data + b'\00' * (256 - remaining)

print(v_start)

for i in range(8):
    print(v_sp % (i, 
            i >> 1,
            i, 4 * i + 3, 4 * i,
            4 * i + 3, 4 * i,
            i, i, i, i, i, i))
    for j in range(len(data) >> 8):
        s = ''
        for k in range(64):
            v = data[256 * j + 4 * k + (i >> 1)]
            v = (v & 0xf) if (i & 1 == 0) else ((v >> 4) & 0xf)
            s = HEX_CHARS[v] + s
        print('    defparam sdpb%d.INIT_RAM_%c%c = 256\'h%s;' % (i, HEX_CHARS[(j >> 4) & 0xf], HEX_CHARS[j & 0xf], s))

print(v_end)
