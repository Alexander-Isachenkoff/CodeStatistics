#include "C:/IAS/C/src/testing/testing.h"
#include "C:/IAS/C/src/utils/num_utils.h"

int main()
{
    int *digits = byDigits(125);

    assertEquals(5, digits[0]);
    assertEquals(2, digits[1]);
    assertEquals(1, digits[2]);

    int expected[] = {5, 2, 1};
    assertArraysEquals(expected, digits, 3);

    digits = byDigits2(89);
    int expected4[] = {9, 8};
    assertArraysEquals(expected4, digits, 2);

    digits = byDigits3(789);
    int expected2[] = {9, 8, 7};
    assertArraysEquals(expected2, digits, 3);

    digits = byDigits4(1789);
    int expected3[] = {9, 8, 7, 1};
    assertArraysEquals(expected3, digits, 4);

    assertEqualsF(1, round0(1.15));
    assertEqualsF(2, round0(1.86));
    assertEqualsF(3, round0(3.499));
    assertEqualsF(4, round0(3.5));
    assertEqualsF(5, round0(4.71));
    assertEqualsF(5, round0(5.0));
    assertEqualsF(4, round0(4.00001));

    assertEqualsF(4.13, roundTo(4.126, 2));
    assertEqualsF(4.1, roundTo(4.126, 1));
    assertEqualsF(5.0, roundTo(4.967, 1));

    assertEquals(4, abs(4));
    assertEquals(3, abs(-3));

    int arr1[] = {2,3,4,5};
    assertEquals(14, sum(arr1, 4));

    int arr2[] = {2, -3, 4, -5, 6};
    assertEquals(4, sum(arr2, 5));

    assertEqualsF(3.5, avg(arr1, 4));
    assertEqualsF(0.8, avg(arr2, 5));

    int arr3[] = {-2, -3, -4, -1, -5, -6};
    assertEquals(5, maximum(arr1, 4));
    assertEquals(6, maximum(arr2, 5));
    assertEquals(-1, maximum(arr3, 6));

    puts("Press any key to close");
    getch();
}