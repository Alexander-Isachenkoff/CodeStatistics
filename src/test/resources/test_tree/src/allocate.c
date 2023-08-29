#include <stdio.h>
#include <stdlib.h>

#include "C:\IAS\C\src\utils\strutils.h"

int main()
{
    int rows = 4;
    int cols = 5;

    int m1[rows][cols];
    int **m2 = (int **)calloc(rows, sizeof(int *));
    int *m3 = (int *)calloc(rows*cols, sizeof(int));

    int i, j, k = 0;
    
    for (i = 0; i < rows; i++)
    {
        for (j = 0; j < cols; j++)
        {
            m1[i][j] = k++;
        }
    }

    k = 0;
    for (i = 0; i < rows; i++)
    {
        m2[i] = (int *)calloc(cols, sizeof(int));
        for (j = 0; j < cols; j++)
        {
            m2[i][j] = k++;
        }
    }

    k = 0;
    for (i = 0; i < rows; i++)
    {
        for (j = 0; j < cols; j++)
        {
            *(m3 + i*cols + j) = k++;
        }
    }

    matrixToString(&m1, rows, cols, " ");
    matrixToString(m2, rows, cols, " ");

    for (i = 0; i < rows; i++)
    {
        for (j = 0; j < cols; j++)
        {
            // printf("%3d", m2[i][j]);
            printf("%3d\n", *(m1[i] + j));
        }
    }

    puts("");
    puts("Press any key to close...");
    getch();
}
