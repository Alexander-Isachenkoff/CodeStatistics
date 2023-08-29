#ifndef strutils_h
#define strutils_h

#include "strutils.h"

#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "C:\IAS\C\src\utils\num_utils.h"
#include "C:\IAS\C\src\utils\strutils.h"

char *toStr(int val)
{
    static char *s;
    s = (char *)malloc(intLen(val) * sizeof(char));
    sprintf(s, "%d", val);
    return s;
}

char *quote(char *str)
{
    static char *strQ;
    strQ = (char *)calloc((strlen(str) + 2), sizeof(char));
    strcat(strQ, "\"");
    strcat(strQ, str);
    strcat(strQ, "\"");
    return strQ;
}

char *join(char **strings, int size, char *separator)
{
    int len = 0;
    int i;
    for (i = 0; i < size; i++)
    {
        len += strlen(strings[i]);
    }

    static char *result;
    result = (char *)calloc(len + strlen(separator) * (size - 1), sizeof(char));

    for (i = 0; i < size; i++)
    {
        strcat(result, strings[i]);
        if (i < size - 1)
        {
            strcat(result, separator);
        }
    }

    return result;
}

char *arrayToString(int *array, int size, char *separator)
{
    int i;
    int len = 0;
    for (i = 0; i < size; i++)
    {
        len += intLen(array[i]);
    }

    static char *string;
    string = (char *)calloc(len + strlen(separator) * (size - 1), sizeof(char));

    for (i = 0; i < size; i++)
    {
        strcat(string, toStr(array[i]));
        if (i < size - 1)
        {
            strcat(string, separator);
        }
    }

    return string;
}

char *matrixToString(int **matrix, int rows, int cols, char *separator)
{
    char **strings = (char **)malloc(rows * sizeof(char *));
    int i;
    for (i = 0; i < rows; i++)
    {
        strings[i] = arrayToString(matrix[i], cols, separator);
    }

    return join(strings, rows, "\n");
}

#endif