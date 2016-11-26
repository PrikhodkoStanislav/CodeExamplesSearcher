int main(void)
{
  int i;
  char words[20][20]={" c a t ",       " c a r ",     " b e a r ",   " s h i p ",   " m o u s e ",
                      " b e a t l e ", " c o a t ",   " n e s t ",   " i c e ",     " s u g a r ",
                      " b a c o n ",   " f r o w n ", " s m i l e ", " d e a d ",   " f e a t h e r ",
                      " g o a t ",     " h e n ",     " j e l l y ", " k o a l a ", " l i p s "};

  char* token;

  const char *search = " ";
  int random;

  srand(time(NULL));

  for( i=0;i<4;i++)
  {
    printf( "%d: ",i );
    random = rand() % 20;
    const char *blanks="                   ";
    printf("%.*s\"%s\", len is %2zu: token list:  ",(int) (strlen( blanks ) - (strlen(words[random]))),
                                                    blanks, words[random], strlen( words[random] ));
    char *sep = "";
    /**
     * strtok modifies the input string by overwriting the delimiter with
     * the 0 string terminator; to preserve the contents of words[random],
     * we need to copy it to a temporary string, and call strtok on that
     * copy.
     */
    char temp[20];
    strcpy( temp, words[random] );
    token = strtok( temp, search );
    while(token!=NULL)
    {
      printf("%s\"%s\"",sep, token);
      sep = ", ";
      token = strtok(NULL, search);
    }
    putchar( '\n' );
  }

  return 0;
}