#ifndef PRINT_H
#define PRINT_H

void serial_init(void);

void putchar(char ch);
void putstr(const char *str);
void putw(unsigned int n);
void puth(unsigned short n);
void putb(unsigned char n);

#endif
