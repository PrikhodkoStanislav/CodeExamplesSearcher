package ru.compscicenter.practice.searcher.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.prefs.Preferences;

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

    private String result = "<h1>Welcome to the Code Examples Searcher Server!</h1>"
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
            + "<p>Input maximum number of examples:</p>"
            + "<p><input type=\"number\" id=\"maxExamplesNumber\" name=\"maxExamplesNumber\" value=\"%3$d\" min=\"0\"" +
            "max=\"1000\" step=\"1\"></p>"
            + "<p>Do you want restore DB before searching?</p>"
            + "<p><input type=\"radio\" id = \"restoreDB\" name=\"restoreDB\" value=\"true\" %4$s/>"
            + "Yes"
            + "<br />"
            + "<input type=\"radio\" id = \"restoreDB\" name=\"restoreDB\" value=\"false\" %5$s/>"
            + "No"
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

    public void setPreferences(String path, long timeout, int maxExamplesNumber, boolean restoreDB) {
        String ID1 = "path";
        String ID2 = "timeout";
        String ID3 = "maxExamplesNumber";
        String ID4 = "restoreDB";

        prefs.put(ID1, path);
        prefs.putLong(ID2, timeout);
        prefs.putInt(ID3, maxExamplesNumber);
        prefs.putBoolean(ID4, restoreDB);
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        logger.setLevel(Level.INFO);

        final String defaultPath = "./";
        final long defaultTimeout = 10000;
        final int defaultMaxExamplesNumber = 20;
        final boolean defaultRestoreDB = false;

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
            long timeout = prefs.getLong("timeout", defaultTimeout);
            int maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
            boolean restoreDB = prefs.getBoolean("restoreDB", defaultRestoreDB);
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
            long length = result.length();
            response.setContentLengthLong(length);
            response.getWriter().println(result);
        } else if (uri.contains("/settings")) {
            String path = defaultPath;
            long timeout = defaultTimeout;
            int maxExamplesNumber = defaultMaxExamplesNumber;
            boolean restoreDB = defaultRestoreDB;
            if (uri.equals("/settings/update_settings")) {
                path = request.getParameter("path");
                String timeoutStr = request.getParameter("timeout");
                timeout = Long.parseLong(timeoutStr);
                String maxExamplesNumberStr = request.getParameter("maxExamplesNumber");
                maxExamplesNumber = Integer.parseInt(maxExamplesNumberStr);
                String restoreDBStr = request.getParameter("restoreDB");
                restoreDB = Boolean.parseBoolean(restoreDBStr);
                setPreferences(path, timeout, maxExamplesNumber, restoreDB);
            } else if (uri.equals("/settings")) {
                path = prefs.get("path", defaultPath);
                timeout = prefs.getLong("timeout", defaultTimeout);
                maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
                restoreDB = prefs.getBoolean("restoreDB", defaultRestoreDB);
            }
            String checkedYes = "";
            String checkedNo = "";
            if (restoreDB) {
                checkedYes = "checked";
            } else {
                checkedNo = "checked";
            }
            String result = String.format(settingsResult, path, timeout, maxExamplesNumber, checkedYes, checkedNo);
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
