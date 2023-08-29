#include <stdio.h>
#include <stdlib.h>

#include "C:\IAS\C\src\utils\strutils.h"

// #define INPUT

int main()
{
    int rows = 4;
    int cols = 5;

    #ifdef INPUT
        puts("Input rows count");
        scanf("%d", &rows);
        puts("Input cols count");
        scanf("%d", &cols);
    #endif

    int **matrix = (int **)calloc(rows, sizeof(int *));
    char **pathMatrix = (char **)calloc(rows, sizeof(char *));
    int i, j;
    for (i = 0; i < rows; i++)
    {
        matrix[i] = (int *)calloc(cols, sizeof(int));
        pathMatrix[i] = (char *)calloc(cols, sizeof(char));
    }

    int k = i = j = 0;

    int vx = 1;
    int vy = 0;
    char c = 26;

    while (k < cols * rows)
    {
        matrix[i][j] = ++k;

        if (vx == 1 && (j == cols - 1 || matrix[i][j + 1] != 0))
        {
            vx = 0;
            vy = 1;
            c = 25;
        }
        if (vy == 1 && (i == rows - 1 || matrix[i + 1][j] != 0))
        {
            vx = -1;
            vy = 0;
            c = 27;
        }
        if (vx == -1 && (j == 0 || matrix[i][j - 1] != 0))
        {
            vx = 0;
            vy = -1;
            c = 24;
        }
        if (vy == -1 && (i == 0 || matrix[i - 1][j] != 0))
        {
            vx = 1;
            vy = 0;
            c = 26;
        }

        j += vx;
        i += vy;
    }

    puts(matrixToString(matrix, rows, cols, " "));

    for (i = 0; i < rows; i++)
    {
        for (j = 0; j < cols; j++)
        {
            printf("%3d", matrix[i][j]);
        }
        printf("\n");
    }

    puts("Press any key to close...");
    getch();
}
