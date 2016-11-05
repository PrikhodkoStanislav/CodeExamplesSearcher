char *buffer = NULL;

int mystrcat(char *addition)
{
   buffer = realloc(buffer, strlen(buffer) + strlen(addition) + sizeof(char));
   if (!buffer)
     return(FAIL);
   strcat(buffer, addition);
   return(OK);
}