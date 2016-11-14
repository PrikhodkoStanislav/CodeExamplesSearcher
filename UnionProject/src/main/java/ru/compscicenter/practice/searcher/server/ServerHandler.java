package ru.compscicenter.practice.searcher.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final static Logger logger = Logger.getLogger(SelfProjectSearcher.class);

    private String result = "<h1>Welcome to the Code Examples Searcher Server!</h1>";

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        logger.setLevel(Level.INFO);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String uri = request.getRequestURI();
        if (uri.equals("/set_example")) {
            String funcName = request.getParameter("func");
            try {
                result = MainSearcher.searchExamples(funcName);
            } catch (ParseException e) {
                logger.error("Sorry, something wrong!", e);
//                e.printStackTrace();
            }
        }
        else if (uri.equals("/get_example")) {
            int length = result.length();
            response.setContentLength(length);
            response.getWriter().println(result);
        }
    }
}
