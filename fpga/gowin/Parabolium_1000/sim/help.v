
// trivial PLL
module Gowin_rPLL (
	output	clkout,
	output	lock,
	input	reset,
	input	clkin
);

	assign lock = 1'b1;
	assign clkout = clkin;

endmodule;

