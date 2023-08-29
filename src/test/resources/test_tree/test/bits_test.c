#include <stdio.h>

#include "../src/bits/bits.h"
#include "../src/testing/testing.h"

int main()
{
    int a = 0b00001100;
    
    put1(&a, 6); // 01001100
    assertEquals(0b01001100, a);

    put0(&a, 2); // 01001000
    assertEquals(0b01001000, a);

    assertStringsEquals("01001000", toBinaryString8(a));

    assertEquals(1, getBit(0b00000001, 0));
    assertEquals(1, getBit(0b00000110, 1));
    assertEquals(0, getBit(0b01010100, 1));
    assertEquals(1, getBit(0b01010100, 4));
    assertEquals(0, getBit(0b01010110, 7));

    puts("Press any key to close");
    getch();
}


