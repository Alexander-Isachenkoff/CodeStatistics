#include "C:\IAS\C\src\utils\strutils.h"
#include "C:\IAS\C\src\testing\testing.h"

void intLenTest()
{
    assertEquals(1, intLen(0));
    assertEquals(1, intLen(1));
    assertEquals(2, intLen(25));
    assertEquals(3, intLen(320));
}

void toStrTest()
{
    assertStringsEquals("1", toStr(1));
    assertStringsEquals("12", toStr(12));
    assertStringsEquals("123", toStr(123));
}

void quoteTest()
{
    assertStringsEquals("\"1\"", quote("1"));
    assertStringsEquals("\"12\"", quote("12"));
    assertStringsEquals("\"123\"", quote("123"));
}

void arrayToStringTest()
{
    int arr1[] = {1, 2, 3, 4, 5};
    assertStringsEquals("1, 2, 3, 4, 5", arrayToString(arr1, 5, ", "));

    int arr2[] = {1, 20, 35, 142, 51, 85, 2500, 854};
    assertStringsEquals("1, 20, 35, 142, 51, 85, 2500, 854", arrayToString(arr2, 8, ", "));
}

void joinTest()
{
    char *strings[] = {"hello", "world", "my", "123"};
    assertStringsEquals("hello, world, my, 123", join(strings, 4, ", "));
}

int main()
{
    intLenTest();
    toStrTest();
    quoteTest();
    arrayToStringTest();
    joinTest();

    puts("Press any key to close");
    getch();
}