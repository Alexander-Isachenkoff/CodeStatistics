#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define N 10000

int main()
{
    srand(time(NULL));

    int i;
    int totalChildren = 0;
    for (i = 0; i < N; i++) {
        int a;
        // printf("%d - ", i+1);
        do {
            a = rand() % 2;
            // printf("%d", a);
            totalChildren++;
        } while (a != 1);
        // printf("\n");
    }

    printf("totalChildren = %d\n", totalChildren);
    printf("avg for family = %f\n", totalChildren / (float) N);
    printf("ratio (boys/total) = %f\n", N / (float)totalChildren);

    puts("Press any key to close...");
    getch();
}
