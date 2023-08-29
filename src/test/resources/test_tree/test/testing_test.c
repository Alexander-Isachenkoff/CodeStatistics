#include <stdio.h>

#include "C:\IAS\C\src\testing\testing.h"

int main()
{
    assertEquals(1, 1);
    assertEquals(1, 2);
    assertEquals(2, 2);

    assertStringsEquals("aa", "aa");
    assertStringsEquals("aaa", "bbb");

    puts("Press any key to close");
    getch();
}