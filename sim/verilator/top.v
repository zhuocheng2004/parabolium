
`include "dpi-c.v"


module CommitRaw(
        input           commit,
        input [31:0]    commit_pc,
        input [31:0]    next_pc,
        input           rf_wen,
        input [4:0]     rf_waddr,
        input [31:0]    rf_wdata
    );
    always @(posedge commit) begin
        sim_commit();
    end
endmodule

module ErrorRaw(
        input           error,
        input [2:0]     error_type,
        input [31:0]    info0
    );
    always @(posedge error) begin
        sim_error({ 5'b0, error_type }, info0);
    end
endmodule

module StopRaw(
        input   stop
    );
    always @(posedge stop) begin
        sim_stop();
    end
endmodule


module top(
        input	clk,
        input	rst
    );

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

    wire	    uart_tx;

    Core core (
             .clock(clk),
             .reset(rst),

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

    reg [31:0]  waddr;

    always @(posedge clk) begin
        if (ram_awready && ram_awvalid)
            waddr <= ram_awaddr;
    end

    localparam [1:0] OKAY = 2'b00, SLVERR = 2'b10, DECERR = 2'b11;

    localparam [1:0] IDLE = 2'b00, DATA = 2'b01, DELAY = 2'b10, WAIT_READY = 2'b11;

    // READ

    reg [1:0] r_delay_cnt;
    reg [1:0] r_state, r_state_next;
    always @(*) begin
        casez (r_state)
            IDLE:
                r_state_next = ram_arvalid ? DELAY : IDLE;
            DELAY:
                r_state_next = r_delay_cnt == 0 ? WAIT_READY : DELAY;
            WAIT_READY:
                r_state_next = ram_rready ? IDLE : WAIT_READY;
            default:
                r_state_next = IDLE;
        endcase
    end
    always @(posedge clk) begin
        if (rst)
            r_state <= IDLE;
        else
            r_state <= r_state_next;

        if (r_state == IDLE && ram_arvalid)
            r_delay_cnt <= $random()[1:0];
        else if (r_state == DELAY)
            r_delay_cnt <= r_delay_cnt - 1;
    end

    assign ram_arready  = r_state == IDLE;
    assign ram_rvalid   = r_state == WAIT_READY;

    wire r_good = { ram_araddr[31:14], 14'b0 } == 32'h80000000;

    always @(posedge clk) begin
        if (ram_arready && ram_arvalid) begin
            if (r_good) begin
                ram_rdata <= sim_ram_read(ram_araddr);
                ram_rresp <= r_good ? OKAY : DECERR;
            end
        end
    end

    // WRITE

    reg [1:0] w_delay_cnt;
    reg [1:0] w_state, w_state_next;
    always @(*) begin
        casez (w_state)
            IDLE:
                w_state_next = ram_awvalid ? DATA: IDLE;
            DATA:
                w_state_next = ram_wvalid ? DELAY : DATA;
            DELAY:
                w_state_next = w_delay_cnt == 0 ? WAIT_READY : DELAY;
            WAIT_READY:
                w_state_next = ram_bready ? IDLE : WAIT_READY;
            default:
                w_state_next = IDLE;
        endcase
    end
    always @(posedge clk) begin
        if (rst)
            w_state <= IDLE;
        else
            w_state <= w_state_next;

        if (w_state == DATA && ram_wvalid)
            w_delay_cnt <= $random()[1:0];
        else if (w_state == DELAY)
            w_delay_cnt <= w_delay_cnt - 1;
    end

    assign ram_awready  = w_state == IDLE;
    assign ram_wready   = w_state == DATA;
    assign ram_bvalid   = w_state == WAIT_READY;

    wire w_good = { ram_awaddr[31:14], 14'b0 } == 32'h80000000;

    assign ram_bresp    = w_good ? OKAY : DECERR;

    always @(posedge clk) begin
        if (ram_wready && ram_wvalid) begin
            if (w_good)
                sim_ram_write(waddr, ram_wdata, { 4'h0, ram_wstrb });
        end
    end

endmodule
