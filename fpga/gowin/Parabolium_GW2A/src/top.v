
module top(
        input clk,
        input rst_n,

        output [7:0] led_neg,
        input uart_rx,
        output uart_tx
    );

    wire rst = ~rst_n;

    wire clk2;
    wire lock;
    Gowin_rPLL pll (
                   .clkout(clk2),
                   .lock(lock),
                   .reset(rst),
                   .clkin(clk)
               );

    wire rst2 = rst || !lock;

    wire	    ram_awvalid;
    wire	    ram_awready;
    wire [31:0]	ram_awaddr;
    wire	    ram_wvalid;
    wire	    ram_wready;
    wire [31:0]	ram_wdata;
    wire [3:0]	ram_wstrb;
    wire	    ram_bvalid;
    wire	    ram_bready;
    reg [1:0]	ram_bresp;
    wire 	    ram_arvalid;
    wire	    ram_arready;
    wire [31:0]	ram_araddr;
    wire	    ram_rvalid;
    wire	    ram_rready;
    reg [31:0]	ram_rdata;
    reg [1:0]	ram_rresp;

    wire [7:0]	led;
    wire	    led_en;

    Core core (
             .clock(clk2),
             .reset(rst2),

             .io_ram_awvalid(ram_awvalid),
             .io_ram_awready(ram_awready),
             .io_ram_awaddr(ram_awaddr),
             .io_ram_wvalid(ram_wvalid),
             .io_ram_wready(ram_wready),
             .io_ram_wdata(ram_wdata),
             .io_ram_wstrb(ram_wstrb),
             .io_ram_bvalid(ram_bvalid),
             .io_ram_bready(ram_bready),
             .io_ram_bresp(ram_bresp),
             .io_ram_arvalid(ram_arvalid),
             .io_ram_arready(ram_arready),
             .io_ram_araddr(ram_araddr),
             .io_ram_rvalid(ram_rvalid),
             .io_ram_rready(ram_rready),
             .io_ram_rdata(ram_rdata),
             .io_ram_rresp(ram_rresp),

             .io_led(led),
             .io_led_en(led_en),

             .io_uart_tx(uart_tx));
endmodule
