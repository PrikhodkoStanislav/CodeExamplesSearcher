set /p function="Input function:"
set /p p="Input path to the file:"
java -jar target\searcher-1.0-SNAPSHOT.jar %function% %p%
set /p end="Enter"