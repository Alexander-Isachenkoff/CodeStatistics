#include <stdio.h>

float my_abs(float val)
{
    return (val < 0) ? -val : val;
}

float my_sqrt(float val)
{
    float a1 = 0;
    float a2 = val;
    float e;
    do
    {
        e = (a2 + a1) / 2;
        if (e * e > val)
        {
            a2 = e;
        }
        else if (e * e < val)
        {
            a1 = e;
        }
        printf("a1 = %f; a2 = %f; e = %f\n", a1, a2, e);
    } while (my_abs(e * e - val) > 0.000001);
    return e;
}

int main()
{
    float a = 1250;

    printf("%f\n", my_sqrt(a));

    puts("Press any key to close...");
    getch();
}
