 //main
         int size=10;
            char *string= (char*) malloc (sizeof(char)*15);

            scanf("%s",string);
            findAllReplacements(NULL,10,string);

//at findAllReplacements

void findAllReplacements(nameInfoT* names,int size,char* expression){
    int ssize=strlen(expression);
    printf("%stringsize:%d\n",ssize);