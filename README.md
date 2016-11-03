# CodeExamplesSearcher
usage: search -options [<result_format>]
Code example searcher
 -a,--all                Search code examples both on web sites and in a project
 -f,--func <func_name>   Function name, required
 -help,--help            All functions of this utility
 -p,--path <path_name>   Path to project
 -s,--offline            Search code examples only in the a project
 -w,--online             Search code examples only on web sites
In order to execute searcher needs one of three options: -a, -s or -w

Result format can be:
    html        return HTML-file
    txt         return TXT-file
    no type     print results on the screen
