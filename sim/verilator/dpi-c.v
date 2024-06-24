
/*
 * for sim_*_ok and its return byte:
 *   bit [0]   :  1 -> finished, 0 -> waiting,
 *   bit [2:1] :  AXI resp
 */

import "DPI-C" function void sim_read(input int raddr);
import "DPI-C" function byte sim_read_ok(output int rdata);
import "DPI-C" function void sim_write(input int waddr, input int wdata, input byte wmask);
import "DPI-C" function byte sim_write_ok();

import "DPI-C" function void sim_stop(input byte ebreak);
import "DPI-C" function void sim_error(input byte error_type, input int info0, input int info1);
import "DPI-C" function void sim_commit();
