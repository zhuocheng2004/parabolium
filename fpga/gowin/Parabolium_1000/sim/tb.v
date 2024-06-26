
`timescale 1ns/1ns

task finish (
        input [31:0] clk_cnt
    );
    begin
        $display("\n==== IVERILOG SIM END ====  ");
        $display("  CLOCK COUNT : %d", clk_cnt);
        $finish;
    end
endtask

module tb;

    reg clk = 0;
    reg rst = 1;

    wire [7:0]      led_neg;

    wire            uart_tx;
    wire            uart_rx;

    reg [31:0] clk_cnt = 0;
    always @(posedge clk) begin
        if (rst) begin
            clk_cnt <= 0;
        end
        clk_cnt <= clk_cnt + 1;
    end

    always #10 clk=~clk;
    initial begin
        $display("==== IVERILOG SIM BEGIN ====  ");
        #100        rst = 0;
        #50000      finish(clk_cnt);
    end

    initial
    begin
        $dumpfile("wave.vcd");
        $dumpvars(0, tb);
    end

    top t (
            .clk(clk),
            .rst_n(~rst),

            .led_neg(led_neg),

            .uart_tx(uart_tx),
            .uart_rx(uart_rx)
        );
endmodule;
