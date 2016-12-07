package ru.compscicenter.practice.searcher.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.prefs.Preferences;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.compscicenter.practice.searcher.MainSearcher;

/**
 * Created by Станислав on 08.11.2016.
 */
public class ServerHandler extends AbstractHandler {
    private final static Logger logger = Logger.getLogger(ServerHandler.class);

    private String result = ""
            + "<h1>Welcome to the Code Examples Searcher Server!</h1>"
            + "<p>Try to search examples from Sublime.</p>"
            + "<a href=\"http://localhost:8080/settings\">Settings for searcher</a>";

    private String settingsResult = ""
            + "<form action=\"http://localhost:8080/settings/update_settings\">"
            + "<h1>Input settings:</h1>"
            + "<p>Input path to the directory for search (without quotes):</p>"
            + "<p><input type=\"text\" id=\"path\" name=\"path\" value=\"%1$s\" size=\"100\"></p>"
            + "<p>Input timeout for database updating:</p>"
            + "<p><input type=\"number\" id=\"timeout\" name=\"timeout\" value=\"%2$d\" min=\"0\"" +
            "max=\"5000000000\" step=\"1\"></p>"
            + "<p>Input maximum number of examples per resource:</p>"
            + "<p><input type=\"number\" id=\"maxExamplesNumber\" name=\"maxExamplesNumber\" value=\"%3$d\" min=\"0\"" +
            "max=\"1000\" step=\"1\"></p>"
            + "<p>Do you want to restore DB before searching?</p>"
            + "<p><input type=\"radio\" id = \"restoreDB\" name=\"restoreDB\" value=\"true\" %4$s/>"
            + "Yes"
            + "<br />"
            + "<input type=\"radio\" id = \"restoreDB\" name=\"restoreDB\" value=\"false\" %5$s/>"
            + "No"
            + "</p>"
            + "<p>Which sites do you want to include?</p>"
            + "<p><input type=\"checkbox\" id = \"cpp\" name=\"cpp\" value=\"true\" %6$s/>"
            + "cplusplus.com"
            + "<br />"
            + "<input type=\"checkbox\" id = \"cppref\" name=\"cppref\" value=\"true\" %7$s/>"
            + "cppreference.com"
            + "<br />"
            + "<input type=\"checkbox\" id = \"searchCode\" name=\"searchCode\" value=\"true\" %8$s/>"
            + "searchcode.com"
            + "<br />"
            + "<input type=\"checkbox\" id = \"stackOverflow\" name=\"stackOverflow\" value=\"true\" %9$s/>"
            + "stackoverflow.com"
            + "</p>"
            + "<p>Do you want to include DB?</p>"
            + "<p><input type=\"radio\" id = \"includeDB\" name=\"includeDB\" value=\"true\" %10$s/>"
            + "Yes"
            + "<br />"
            + "<input type=\"radio\" id = \"includeDB\" name=\"includeDB\" value=\"false\" %11$s/>"
            + "No"
            + "</p>"
            + "<p>What kind of code duplicator do you want to use?</p>"
            + "<p><input type=\"radio\" id = \"duplicator\" name=\"duplicator\" value=\"1\" %12$s/>"
            + "Levenshtein distance"
            + "<br />"
            + "<input type=\"radio\" id = \"duplicator\" name=\"duplicator\" value=\"2\" %13$s/>"
            + "Equals tokens"
            + "<br />"
            + "<input type=\"radio\" id = \"duplicator\" name=\"duplicator\" value=\"3\" %14$s/>"
            + "Exclude"
            + "</p>"
            + "<p>What kind of beautifier do you want to use?</p>"
            + "<p><input type=\"radio\" id = \"formatter\" name=\"formatter\" value=\"1\" %15$s/>"
            + "AStyle"
            + "<br />"
            + "<input type=\"radio\" id = \"formatter\" name=\"formatter\" value=\"2\" %16$s/>"
            + "Eclipse"
            + "<br />"
            + "<input type=\"radio\" id = \"formatter\" name=\"formatter\" value=\"3\" %17$s/>"
            + "Exclude"
            + "</p>"
            + "<p><input type=\"submit\" value = \"Submit\"></p>"
//            + "<p><input type=\"button\" id=\"button\" onclick=\"f_click();\"" +
//            "value=\"Submit\"></p>"
//            + "<script>"
//            + "function f_click() {"
////            + "document.getElementById(\"path\").value;"
////            + "document.getElementById(\"timeout\").value;"
////            + "document.getElementById(\"timeout\").value = 10000;"
//            + "}"
//            + "</script>"
            + "</form>"
            + "<a href=\"http://localhost:8080/get_examples\">Page with examples</a>";

    private Preferences prefs = Preferences.userRoot().node("settings");

    public void setPreferences(String path, long timeout, int maxExamplesNumber,
                               boolean restoreDB, boolean cpp, boolean cppref,
                               boolean searchCode, boolean stackOverflow,
                               boolean includeDB,
                               int duplicator, int formatter) {
        String ID1 = "path";
        String ID2 = "timeout";
        String ID3 = "maxExamplesNumber";
        String ID4 = "restoreDB";
        String ID5 = "cpp";
        String ID6 = "cppref";
        String ID7 = "searchCode";
        String ID8 = "stackOverflow";
        String ID9 = "includeDB";
        String ID10 = "duplicator";
        String ID11 = "formatter";

        prefs.put(ID1, path);
        prefs.putLong(ID2, timeout);
        prefs.putInt(ID3, maxExamplesNumber);
        prefs.putBoolean(ID4, restoreDB);
        prefs.putBoolean(ID5, cpp);
        prefs.putBoolean(ID6, cppref);
        prefs.putBoolean(ID7, searchCode);
        prefs.putBoolean(ID8, stackOverflow);
        prefs.putBoolean(ID9, includeDB);
        prefs.putInt(ID10, duplicator);
        prefs.putInt(ID11, formatter);
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        logger.setLevel(Level.INFO);

        final String defaultPath = "./";
        final long defaultTimeout = 10000;
        final int defaultMaxExamplesNumber = 20;

        final boolean defaultRestoreDB = false;

        final boolean defaultCpp = true;
        final boolean defaultCppref = true;
        final boolean defaultSearchCode = true;
        final boolean defaultStackOverflow = true;

        final boolean defaultIncludeDB = true;

        final int defaultDuplicator = 1;
        final int defaultFormatter = 1;

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String uri = request.getRequestURI();

        if (uri.equals("/set_example")) {
            String funcName = request.getParameter("func");
            String pathFromSublime = request.getParameter("path");
            String lineStr = request.getParameter("line");
            String string = request.getParameter("string");
            int line = Integer.parseInt(lineStr);
            String pathForSearch = prefs.get("path", defaultPath);
//            long timeout = prefs.getLong("timeout", defaultTimeout);
//            int maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
//            boolean restoreDB = prefs.getBoolean("restoreDB", defaultRestoreDB);
            Thread thread1 = new Thread(() -> {
                try {
                    result = MainSearcher.searchExamplesForClient(funcName, pathForSearch,
                            pathFromSublime, line, string);
                } catch (ParseException e) {
                    logger.error("Parse exception!", e);
                } catch (IOException e) {
                    logger.error("IO exception!", e);
                }
            });
            thread1.start();
        } else if (uri.equals("/get_examples")) {
            result = ""
                    + "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js\"></script>"
                    + "<script>"
                    + "update_content()$(document).ready(function (e){"
                    + "var refresher = setInterval(\"update_content();\", 250);"
                    + "})"
                    + "function update_content()"
                    + "{ $.ajax("
                    + "{type: \"GET\", url: \"http://localhost:8080/get_examples\", timeout: 10000, cache: false, })"
                    + ".done(function (page_html)"
                    + "{ var newDoc = document.documentElement.innerHTML;"
                    + " if (page_html != newDoc)"
                    + "{alert(\"LOADED\");"
                    + " var newDoc = document.open(\"text/html\", \"replace\");"
                    + " newDoc.write(page_html);"
                    + " newDoc.close();}});}"
                    + "</script>"
                    + result;
            long length = result.length();
            response.setContentLengthLong(length);
            response.getWriter().println(result);
        } else if (uri.contains("/settings")) {
            String path = defaultPath;
            long timeout = defaultTimeout;
            int maxExamplesNumber = defaultMaxExamplesNumber;

            boolean restoreDB = defaultRestoreDB;

            boolean cpp = defaultCpp;
            boolean cppref = defaultCppref;
            boolean searchCode = defaultSearchCode;
            boolean stackOverflow = defaultStackOverflow;
            boolean includeDB = defaultIncludeDB;

            int duplicator = defaultDuplicator;
            int formatter = defaultFormatter;

            if (uri.equals("/settings/update_settings")) {
                path = request.getParameter("path");
                String timeoutStr = request.getParameter("timeout");
                timeout = Long.parseLong(timeoutStr);

                String maxExamplesNumberStr = request.getParameter("maxExamplesNumber");
                maxExamplesNumber = Integer.parseInt(maxExamplesNumberStr);

                String restoreDBStr = request.getParameter("restoreDB");
                restoreDB = Boolean.parseBoolean(restoreDBStr);

                String cppStr = request.getParameter("cpp");
                cpp = Boolean.parseBoolean(cppStr);
                String cpprefStr = request.getParameter("cppref");
                cppref = Boolean.parseBoolean(cpprefStr);
                String searchCodeStr = request.getParameter("searchCode");
                searchCode = Boolean.parseBoolean(searchCodeStr);
                String stackOverflowStr = request.getParameter("stackOverflow");
                stackOverflow = Boolean.parseBoolean(stackOverflowStr);

                String includeDBStr = request.getParameter("includeDB");
                includeDB = Boolean.parseBoolean(includeDBStr);

                String duplicatorStr = request.getParameter("duplicator");
                duplicator = Integer.parseInt(duplicatorStr);
                String formatterStr = request.getParameter("formatter");
                formatter = Integer.parseInt(formatterStr);

                setPreferences(path, timeout, maxExamplesNumber, restoreDB,
                        cpp, cppref, searchCode, stackOverflow, includeDB, duplicator, formatter);
            } else if (uri.equals("/settings")) {
                path = prefs.get("path", defaultPath);
                timeout = prefs.getLong("timeout", defaultTimeout);
                maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
                restoreDB = prefs.getBoolean("restoreDB", defaultRestoreDB);
                cpp = prefs.getBoolean("cpp", defaultCpp);
                cppref = prefs.getBoolean("cppref", defaultCppref);
                searchCode = prefs.getBoolean("searchCode", defaultSearchCode);
                stackOverflow = prefs.getBoolean("stackOverflow", defaultStackOverflow);
                includeDB = prefs.getBoolean("includeDB", defaultIncludeDB);
                duplicator = prefs.getInt("duplicator", defaultDuplicator);
                formatter = prefs.getInt("formatter", defaultFormatter);
            }
            String checked = "checked";
            String checkedYes = "";
            String checkedNo = "";
            if (restoreDB) {
                checkedYes = checked;
            } else {
                checkedNo = checked;
            }
            String checkedcpp = "";
            String checkedcppref = "";
            String checkedsearchcode = "";
            String checkedstackoverflow = "";
            if (cpp) {
                checkedcpp = checked;
            }
            if (cppref) {
                checkedcppref = checked;
            }
            if (searchCode) {
                checkedsearchcode = checked;
            }
            if (stackOverflow) {
                checkedstackoverflow = checked;
            }
            String checkedDBYes = "";
            String checkedDBNo = "";
            if (includeDB) {
                checkedDBYes = checked;
            } else {
                checkedDBNo = checked;
            }
            String checkedDuplicator1 = "";
            String checkedDuplicator2 = "";
            String checkedDuplicator3 = "";
            String checkedFormatter1 = "";
            String checkedFormatter2 = "";
            String checkedFormatter3 = "";
            switch (duplicator) {
                case 1:
                    checkedDuplicator1 = checked;
                    break;
                case 2 :
                    checkedDuplicator2 = checked;
                    break;
                case 3 :
                    checkedDuplicator3 = checked;
                    break;
            }
            switch (formatter) {
                case 1:
                    checkedFormatter1 = checked;
                    break;
                case 2 :
                    checkedFormatter2 = checked;
                    break;
                case 3 :
                    checkedFormatter3 = checked;
                    break;
            }

            String result = String.format(settingsResult, path, timeout, maxExamplesNumber,
                    checkedYes, checkedNo,
                    checkedcpp, checkedcppref, checkedsearchcode, checkedstackoverflow,
                    checkedDBYes, checkedDBNo,
                    checkedDuplicator1, checkedDuplicator2, checkedDuplicator3,
                    checkedFormatter1, checkedFormatter2, checkedFormatter3);
            long length = result.length();
            response.setContentLengthLong(length);
            response.getWriter().println(result);
        } else if (uri.equals("/")) {
            String result = "<h1>Welcome!</h1>";
            result += "<p>You should go to the page to get examples.</p>";
            result += "<a href=\"http://localhost:8080/get_examples\">Page with examples</a>";
            long length = result.length();
            response.setContentLengthLong(length);
            response.getWriter().println(result);
        }
    }
}
