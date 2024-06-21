
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
        input	stop
    );
    always @(posedge stop) begin
        sim_stop();
    end
endmodule

module top(
        input   clk,
        input   rst
    );

    wire        mem_awvalid;
    wire        mem_awready;
    wire [31:0] mem_awaddr;
    wire        mem_wvalid;
    wire        mem_wready;
    wire [31:0] mem_wdata;
    wire [3:0]  mem_wstrb;
    wire        mem_bvalid;
    wire        mem_bready;
    reg [1:0]   mem_bresp;
    wire        mem_arvalid;
    wire        mem_arready;
    wire [31:0] mem_araddr;
    wire        mem_rvalid;
    wire        mem_rready;
    reg [31:0]  mem_rdata;
    reg [1:0]   mem_rresp;

    Core core (
             .clock(clk),
             .reset(rst),

             .io_mem_awvalid(mem_awvalid),
             .io_mem_awready(mem_awready),
             .io_mem_awaddr(mem_awaddr),
             .io_mem_wvalid(mem_wvalid),
             .io_mem_wready(mem_wready),
             .io_mem_wdata(mem_wdata),
             .io_mem_wstrb(mem_wstrb),
             .io_mem_bvalid(mem_bvalid),
             .io_mem_bready(mem_bready),
             .io_mem_bresp(mem_bresp),
             .io_mem_arvalid(mem_arvalid),
             .io_mem_arready(mem_arready),
             .io_mem_araddr(mem_araddr),
             .io_mem_rvalid(mem_rvalid),
             .io_mem_rready(mem_rready),
             .io_mem_rdata(mem_rdata),
             .io_mem_rresp(mem_rresp)
         );

    reg [31:0]  waddr;

    always @(posedge clk) begin
        if (mem_awready && mem_awvalid)
            waddr <= mem_awaddr;
    end

    localparam [1:0] IDLE = 2'b00, DATA = 2'b01, WAIT_MEM = 2'b10, WAIT_READY = 2'b11;

    // READ

    reg [7:0] read_ok;

    reg [1:0] r_state, r_state_next;
    always @(*) begin
        casez (r_state)
            IDLE:
                r_state_next = mem_arvalid ? WAIT_MEM : IDLE;
            WAIT_MEM:
                r_state_next = read_ok[0] ? WAIT_READY : WAIT_MEM;
            WAIT_READY:
                r_state_next = mem_rready ? IDLE : WAIT_READY;
            default:
                r_state_next = IDLE;
        endcase
    end
    always @(posedge clk) begin
        if (rst)
            r_state <= IDLE;
        else
            r_state <= r_state_next;
    end

    assign mem_arready  = r_state == IDLE;
    assign mem_rvalid   = r_state == WAIT_READY;

    always @(posedge clk) begin
        if (rst)
            read_ok <= 8'b0;
        else if (r_state == WAIT_MEM && !read_ok[0])
            read_ok <= sim_read_ok(mem_rdata);
        else
            read_ok <= 8'b0;

        if (r_state == WAIT_MEM)
            mem_rresp <= read_ok[2:1];

        if (mem_arready && mem_arvalid)
            sim_read(mem_araddr);
    end

    // WRITE

    reg [7:0] write_ok;

    reg [1:0] w_state, w_state_next;
    always @(*) begin
        casez (w_state)
            IDLE:
                w_state_next = mem_awvalid ? DATA: IDLE;
            DATA:
                w_state_next = mem_wvalid ? WAIT_MEM : DATA;
            WAIT_MEM:
                w_state_next = write_ok[0] ? WAIT_READY : WAIT_MEM;
            WAIT_READY:
                w_state_next = mem_bready ? IDLE : WAIT_READY;
            default:
                w_state_next = IDLE;
        endcase
    end
    always @(posedge clk) begin
        if (rst)
            w_state <= IDLE;
        else
            w_state <= w_state_next;
    end

    assign mem_awready  = w_state == IDLE;
    assign mem_wready   = w_state == DATA;
    assign mem_bvalid   = w_state == WAIT_READY;

    always @(posedge clk) begin
        if (rst)
            write_ok <= 8'b0;
        else if (w_state == WAIT_MEM && !write_ok[0])
            write_ok <= sim_write_ok();
        else
            write_ok <= 8'b0;

        if (mem_wready && mem_wvalid)
            sim_write(waddr, mem_wdata, { 4'h0, mem_wstrb });

        if (w_state == WAIT_MEM)
            mem_bresp <= write_ok[2:1];
    end
endmodule
