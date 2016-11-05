FILE* dict = fopen("/usr/share/dict/words", "r"); //open the dictionary for read-only access
   if(dict == NULL) {
      return;
   }

   int i;
   i = 0;

   // Read each line of the file, and insert the word in hash table
   char word[128];
   while(i < 10 && fgets(word, sizeof(word), dict) != NULL) {
      printf("W:%s L:%d\n", word, (int)strlen(word));

      i++;
   }