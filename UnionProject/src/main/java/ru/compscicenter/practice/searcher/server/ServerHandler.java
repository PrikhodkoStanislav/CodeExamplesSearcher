package ru.compscicenter.practice.searcher.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
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

    private String settingsResult = "<h1>Input settings:</h1>"
            + "<p>Input path to the directory for search:</p>"
            + "<p><input type=\"text\" id=\"path\" name=\"path\" value=\"%1$s\" size=\"50\"></p>"
            + "<p>Input timout for database updating:</p>"
            + "<p><input type=\"number\" id=\"timeout\" name=\"timeout\" value=\"%2$d\" min=\"0\"" +
            "max=\"5000000000\" step=\"1\"></p>"
            + "<p><input type=\"button\" id=\"button\" onclick=\"f_click();\"" +
            "value=\"Submit\"></p>"
            + "<script>"
            + "function f_click() {"
            + "document.getElementById(\"path\").value;"
            + "document.getElementById(\"timeout\").value;"
            + "document.getElementById(\"timeout\").value = 10000;"
            + "}"
            + "</script>"
            + "<a href=\"http://localhost:8080/get_examples\">Page with examples</a>";

    private Preferences prefs = Preferences.userRoot().node("settings");

    public void setPreferences(String path, long timeout) {
        String ID1 = "path";
        String ID2 = "timeout";

        prefs.put(ID1, path);
        prefs.putLong(ID2, timeout);
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        logger.setLevel(Level.INFO);

        final String defaultPath = "./";
        // Directpry with lib.
//        final String defaultPath = "../libcurl/curl-master/lib/";
        final long defaultTimeout = 10000;

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String uri = request.getRequestURI();

        // Set default when server start.
        // Temporarily.
        setPreferences(defaultPath, defaultTimeout);
        if (uri.equals("/set_example")) {
            String funcName = request.getParameter("func");
            String pathFromSublime = request.getParameter("path");
            String lineStr = request.getParameter("line");
            String string = request.getParameter("string");
            int line = Integer.parseInt(lineStr);
            String pathForSearch = prefs.get("path", defaultPath);
            long timeout = prefs.getLong("timeout", defaultTimeout);
            try {
                result = MainSearcher.searchExamplesForClient(funcName, pathForSearch, pathFromSublime, line, string);
            } catch (ParseException e) {
                logger.error("Sorry, something wrong!", e);
            }
        } else if (uri.equals("/get_examples")) {
            long length = result.length();
            response.setContentLengthLong(length);
            response.getWriter().println(result);
        } else if (uri.equals("/settings")) {
            String pathForSearch = prefs.get("path", defaultPath);
            long timeout = prefs.getLong("timeout", defaultTimeout);
            String result = String.format(settingsResult, pathForSearch, timeout);
//            setPreferences(pathForSearch, timeout);

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
