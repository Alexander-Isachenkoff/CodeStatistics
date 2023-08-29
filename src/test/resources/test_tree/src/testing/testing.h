#ifndef testing_h
#define testing_h

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>

#include "C:\IAS\C\src\utils\strutils.h"

#define CONSOLE GetStdHandle(STD_OUTPUT_HANDLE)
#define FOREGROUND_GRAY 0x7
#define FOREGROUND_LIGHTGREEN 0xA

printTrue()
{
    printfc("%s\n", FOREGROUND_LIGHTGREEN, "True");
}

printFalseF(float expected, float actual)
{
    printfc("%s", FOREGROUND_RED, "False");
    printf(" expected: %f, acutal: %f\n", expected, actual);
}

printFalse(char *expected, char *actual)
{
    printfc("%s", FOREGROUND_RED, "False");
    printf(" expected: %s, acutal: %s\n", expected, actual);
}

printfc(char *format, int color, char *value)
{
    SetConsoleTextAttribute(CONSOLE, color);
    printf(format, value);
    SetConsoleTextAttribute(CONSOLE, FOREGROUND_GRAY);
}

void assertEquals(int expected, int actual)
{
    if (expected == actual)
    {
        printTrue();
    }
    else
    {
        printFalse(toStr(expected), toStr(actual));
    }
}

void assertEqualsF(float expected, float actual) {
    if (expected == actual) {
        printTrue();
    } else {
        printFalseF(expected, actual);
    }
}

void assertArraysEquals(int *expected, int *actual, int size)
{
    int i;
    for (i = 0; i < size; i++) {
        if (actual[i] != expected[i]) {
            printFalse(toStr(expected[i]), toStr(actual[i]));
            return;
        }
    }
    printTrue();
}

void assertStringsEquals(char *expected, char *actual)
{
    if (strcmp(expected, actual) == 0)
    {
        printTrue();
    }
    else
    {
        printFalse(quote(expected), quote(actual));
    }
}

#endif