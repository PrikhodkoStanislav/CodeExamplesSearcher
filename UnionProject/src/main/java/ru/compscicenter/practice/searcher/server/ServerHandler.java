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
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;

/**
 * Created by Станислав on 08.11.2016.
 */
public class ServerHandler extends AbstractHandler {
    private final static Logger logger = Logger.getLogger(ServerHandler.class);

    private String result = "<h1>Welcome to the Code Examples Searcher Server!</h1>"
            + "<p>Try to search examples from Sublime.</p>"
            + "<a href=\"http://localhost:8080/settings\">Settings for searcher</a>";

    private String settingsResult = "<h1>Input settings:</h1>"
            + "<input type=\"text\" name=\"path\" value=\"\" size=\"30\">"
            + "<a href=\"http://localhost:8080/get_examples\">Page with examples</a>";

    private Preferences prefs = Preferences.userRoot().node("settings");

    public void setPreferences(String funcName, String path, String format, long timeStamp) {
        String ID1 = "functionName";
        String ID2 = "path";
        String ID3 = "format";
        String ID4 = "timeStamp";

        prefs.put(ID1, funcName);
        prefs.put(ID2, path);
        prefs.put(ID3, format);
        prefs.putLong(ID4, timeStamp);
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        logger.setLevel(Level.INFO);

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
            String pathForSearch = prefs.get("path", "./");
            long defaultTimeStamp = 10000;
            long timeStamp = prefs.getLong("timeStamp", defaultTimeStamp);
            setPreferences(funcName, pathForSearch, "html", timeStamp);
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
            long length = settingsResult.length();
            response.setContentLengthLong(length);
            response.getWriter().println(settingsResult);
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
