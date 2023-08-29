#include <string.h>
#include <stdio.h>

// Даны строки s и s0.Найти количество вхождений строки s0 в строку s

int substringsCount(char* string, char* substring) {
    int i, k = 0;
    for (i = 0; i < strlen(string); i++) {
        if (isSubstring(string, substring, i)) {
            k++;
        }
    }
    return k;
}

int isSubstring(char *string, char *substring, int startIndex) {
    int j;
    for (j = 0; j < strlen(substring); j++)
    {
        if (string[startIndex] == substring[j])
        {
            startIndex++;
        }
        else
        {
            return 0;
        }
    }
    return 1;
}

int main()
{
    char string[] = "1 hello 2hello.";
    char substring[] = "hello";

    int count = substringsCount(string, substring);
    printf("%d\n", count);

    printf("%d\n", substringsCount("helllo ll helllo", "ll"));

    puts("Press any key to close");
    getch();
}