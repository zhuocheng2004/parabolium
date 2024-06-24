
# Version Naming Convention for `Parabolium` Processor Series

Basic Format: `Parabolium ABCD [S]`

`A`, `B`, `C`, and `D` can be any one of the hex digits `0`~`F`. Their respective meanings are explained in the following.

Optional suffix `S` may exist.


## Explanation

### Major Generation: `A`

`A` means the major generation of design. In general, the larger the better.

Some values:

- 1: simple 32-bit multi-cycle or small pipeline processors, with basic peripheral support; limited interrupt support; single hart; smaller energy cost.
  
  suited for IoT systems and learning purposes; no higher privilege levels; no address transformation.
- 2 (draft): in-order 32/64-bit pipeline processors, with a higher performance, full privilege level, and address transformation support; SMP.
  
  able to boot some modern kernels such as Linux. I hope it will be able to boot some non-trivial Linux distributions.
- 3 (draft): out-of-order, multiple-issue, superscalar 32/64-bit processors; higher-performance; SMP; NUMA.
   
  hope to run larger OSs more smoothly.
  
### Minor Generation: `B`

`B` means a sub-generation of design inside the same major generation. 
When major generations are the same, in general, the larger the better.

### Major Design Id: `C`

`C` distinguishes different design instances. Different `C` usually means different micro-architectures or different backends.

### Minor Design Id: `D`

Under the same major design, different `D` means small differences in design or small improvements, but the micro-architecture and backend are the same.

### Suffix: `S`

TODO


## Extended Version Naming

When one of the four numbers >= 16, we use more hex digits and split the digits with underscores.

Example: in `Parabolium 34_13_7` `A`=3, `B`=4, `C`=0x13, `D`=7
