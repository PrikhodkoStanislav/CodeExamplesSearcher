package ru.compscicenter.practice.searcher.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.compscicenter.practice.searcher.MainSearcher;

/**
 * Created by Станислав on 08.11.2016.
 */
public class ServerHandler extends AbstractHandler {
    private String result = "";

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String uri = request.getRequestURI();
        Pattern patternSetExample = Pattern.compile("set_example?func=.*");
        Matcher matcheretExample = patternSetExample.matcher(uri);
        if (matcheretExample.matches()) {
            String funcName = request.getParameter("func");
            try {
                result = MainSearcher.searchExamples(funcName);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if (uri.equals("get_example")) {
            response.getWriter().println(result);
        }
    }
}
