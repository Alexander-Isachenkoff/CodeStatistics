#include <stdio.h>
#include <stdlib.h>

#include "C:\IAS\C\src\utils\strutils.h"

int main()
{
    int rows = 4;
    int cols = 5;

    // puts("Input rows count");
    // scanf("%d", &rows);

    // puts("Input cols count");
    // scanf("%d", &cols);

    int **matrix = (int **)calloc(rows, sizeof(int *));
    int i, j, k = 0;
    for (i = 0; i < rows; i++)
    {
        matrix[i] = (int *)calloc(cols, sizeof(int));
        for (j = 0; j < cols; j++) {
            matrix[i][j] = k++;
        }
    }

    puts(matrixToString(matrix, rows, cols, " "));

    puts("Press any key to close...");
    getch();
}
