char buffer[MAXSIZE] = "";

int mystrcat(char *addition)
{
   if (strlen(buffer) + strlen(addition) + sizeof(char)  >= MaxSize)
     return(FAILED);
   strcat(buffer,addition);
   return(OK);
}