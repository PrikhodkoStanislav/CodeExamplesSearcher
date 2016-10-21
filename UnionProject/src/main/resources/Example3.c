#include <string.h>
#include <iostream>
#include <stdio.h>

int main()
{
	char arr[200];
	arr[10] = 'a';
	arr[100] = 'v';
	int n = 0;
	for (int i = 0; i < 5; i++)
	{
		n = strlen(arr);
		printf(n);
	}
	return 0;
}