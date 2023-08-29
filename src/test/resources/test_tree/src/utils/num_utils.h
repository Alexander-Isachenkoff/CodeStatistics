#ifndef num_utils_h
#define num_utils_h

int *intArray(int len)
{
    return (int *)calloc(len, sizeof(int));
}

int sum(int *array, int len)
{
    int i, s = 0;
    for (i = 0; i < len; i++)
    {
        s += array[i];
    }
    return s;
}

float avg(int *array, int len)
{
    return sum(array, len) / (float)len;
}

// int most(int *array, int len, int* comparator) {
//     int max = 0;
//     if (len > 0)
//     {
//         max = array[0];
//     }
//     int i;
//     for (i = 1; i < len; i++)
//     {
//         if (comparator())
//         {
//             max = array[i];
//         }
//     }
//     return max;
// }

int maximum(int *array, int len)
{
    int max = 0;
    if (len > 0) {
        max = array[0];
    }
    int i;
    for (i = 1; i < len; i++) {
        if (array[i] > max) {
            max = array[i];
        }
    }
    return max;
}

int compare(int a, int b) {
    if (a > b) {
        return 1;
    } else if (a < b) {
        return -1;
    } else {
        return 0;
    }
}

int intLen(unsigned int num)
{
    int len = 0;
    do
    {
        len++;
        num /= 10;
    } while (num > 0);
    return len;
}

int *byDigits2(unsigned int num)
{
    static int digits[2];
    digits[1] = num / 10;
    digits[0] = num % 10;

    return digits;
}

int *byDigits3(unsigned int num)
{
    static int digits[3];
    digits[2] = num / 100;
    digits[1] = (num / 10) % 10;
    digits[0] = num % 10;

    return digits;
}

int *byDigits4(unsigned int num)
{
    static int digits[4];
    digits[3] = num / 1000;
    digits[2] = (num / 100) % 10;
    digits[1] = (num / 10) % 10;
    digits[0] = num % 10;

    return digits;
}

int *byDigits(unsigned int num)
{
    int len = intLen(num);
    static int *digits;
    digits = (int *)calloc(len, sizeof(int));
    int i = 0;
    do
    {
        digits[i] = num % 10;
        num /= 10;
        i++;
    } while (num > 0);

    return digits;
}

int round0(float val)
{
    if (val - (int)val >= 0.5)
    {
        return (int)val + 1;
    }
    else
    {
        return (int)val;
    }
}

float roundTo(float val, int afterPoint)
{
    float mult = 1.0;
    int i;
    for (i = 0; i < afterPoint; i++)
    {
        mult *= 10;
    }
    return round0(val * mult) / mult;
}

int abs(int val)
{
    return (val < 0) ? -val : val;
}

#endif