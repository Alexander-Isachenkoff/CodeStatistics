#ifndef bits_h
#define bits_h

#include <string.h>

int bit(int pos)
{
    return 1 << pos;
}

int getBit(int val, int pos)
{
    return (val & bit(pos)) != 0;
}

void put1(int *a, int pos)
{
    *a = set1(*a, pos);
}

void put0(int *a, int pos)
{
    *a = set0(*a, pos);
}

int set1(int a, int pos)
{
    return a | bit(pos);
}

int set0(int a, int pos)
{
    return a & ~bit(pos);
}

char *toBinaryString8(int val)
{
    static char result[8];
    toBinaryString(val, result, 8);
    return result;
}

toBinaryString(int val, char result[], int len)
{
    int i;
    for (i = 0; i < len; i++)
    {
        result[len - i - 1] = getBit(val, i) + '0';
    }
}

struct PORT {
    int port;
    int pin;
    int (*read)(int);
    void (*set)(int);
};

#endif
