
module top(
        input clk,
        input rst_n,

        output [7:0]    led_neg,

        output  uart_tx,
        input   uart_rx
    );

    reg [3:0] rst_cnt;

    always @(posedge clk) begin
        if (~rst_n)
            rst_cnt <= 4'hf;
        else if (rst_cnt != 4'h0)
            rst_cnt <= rst_cnt - 1;
    end

    wire rst = rst_cnt != 4'h0;

    wire clk2;
    wire lock;
    Gowin_rPLL pll (
                   .clkin(clk),
                   .clkout(clk2),
                   .lock(lock)
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

    Tile tile (
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

             .io_uart_tx(uart_tx));

    assign led_neg = ~led;

    wire            ren;
    reg [11:0]      raddr;

    wire            wen;
    reg [11:0]      waddr;
    reg [31:0]      wdata;
    reg [3:0]       wmask;

    RAM ram (
            .clk(clk2),

            .ren(ren),
            .raddr(raddr),
            .rdata(ram_rdata),

            .wen(wen),
            .waddr(waddr),
            .wdata(wdata),
            .wmask(wmask));

    always @(posedge clk2) begin
        if (ram_arready && ram_arvalid)
            raddr <= ram_araddr[13:2];

        if (ram_awready && ram_awvalid)
            waddr <= ram_awaddr[13:2];

        if (ram_wready && ram_wvalid) begin
            wdata <= ram_wdata;
            wmask <= ram_wstrb;
        end
    end

    localparam [1:0] AXI5_OKAY = 2'b00, AXI5_SLVERR = 2'b10, AXI5_DECERR = 2'b11;

    localparam [1:0] IDLE = 2'b00, DATA = 2'b01, ACTION = 2'b10, WAIT_READY = 2'b11;

    // READ

    reg [1:0] r_state, r_state_next;
    always @(*) begin
        casez (r_state)
            IDLE:
                r_state_next = ram_arvalid ? ACTION : IDLE;
            ACTION:
                r_state_next = WAIT_READY;
            WAIT_READY:
                r_state_next = ram_rready ? IDLE : WAIT_READY;
            default:
                r_state_next = IDLE;
        endcase
    end
    always @(posedge clk2) begin
        if (rst)
            r_state <= IDLE;
        else
            r_state <= r_state_next;
    end

    assign ram_arready  = r_state == IDLE;
    assign ram_rvalid   = r_state == WAIT_READY;
    assign ram_rresp    = AXI5_OKAY;

    assign ren          = r_state == ACTION;

    // WRITE

    reg [1:0] w_state, w_state_next;
    always @(*) begin
        casez (w_state)
            IDLE:
                w_state_next = ram_awvalid ? DATA : IDLE;
            DATA:
                w_state_next = ram_wvalid ? ACTION : DATA;
            ACTION:
                w_state_next = WAIT_READY;
            WAIT_READY:
                w_state_next = ram_bready ? IDLE : WAIT_READY;
            default:
                w_state_next = IDLE;
        endcase
    end
    always @(posedge clk2) begin
        if (rst)
            w_state <= IDLE;
        else
            w_state <= w_state_next;
    end

    assign ram_awready  = w_state == IDLE;
    assign ram_wready   = w_state == DATA;
    assign ram_bvalid   = w_state == WAIT_READY;
    assign ram_bresp    = AXI5_OKAY;

    assign wen          = w_state == ACTION;

endmodule
