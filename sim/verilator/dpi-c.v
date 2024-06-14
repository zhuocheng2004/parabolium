
import "DPI-C" function int  sim_ram_read(input int raddr);
import "DPI-C" function void sim_ram_write(input int waddr, input int wdata, input byte wmask);

import "DPI-C" function void sim_stop();
import "DPI-C" function void sim_error(input byte error_type, input int info0);
import "DPI-C" function void sim_commit();

import "DPI-C" function void sim_putchar(input byte ch);
