# Code examples searcher
### Search code examples of user input functions in real time for IDE Sublime.

`usage: search -options <function> [<path>] [<result_format>]`
Code example searcher
 - `-a, --all`           Search code examples both on web sites and in a project
 - `-help, --help`       All functions of this utility
 - `-s, --offline`       Search code examples only in the a project
 - `-server, --server`   Load Jetty server
 - `-w, --online`        Search code examples only on web sites
In order to execute searcher needs one of three options: `-a`, `-s` or `-w`

Result format can be:
 - **html**        return HTML-file
 - **txt**         return TXT-file
 - **no type**     print results on the screen

To use server **jetty** you should execute all files from folder `UnionProject/plugin for Sublime`
to the directory `/{username}/AppData/Roaming/Sublime Text 3/Packages/User`.
You can use install.bat on Windows platform.

Sublime HotKey: `Ctrl+F5` on the current line.
