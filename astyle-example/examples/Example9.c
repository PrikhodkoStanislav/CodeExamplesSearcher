int main() {
    char idCard[MAX_ID_LEN+1];
    printf("ID: ");
    if (fgets(idCard, MAX_ID_LEN+1, stdin)) {
        char *cLast = idCard + strlen(idCard)-1;
        if (*cLast == '\n')
            *cLast = 0;  // strip newline *if* present
        printf("Success!  idCard='%s'\n", idCard);
    } else {
        printf("Failure.\n");
    }
    return 0;
}