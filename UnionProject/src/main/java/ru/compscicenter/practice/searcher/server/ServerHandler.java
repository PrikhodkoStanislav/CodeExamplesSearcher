package ru.compscicenter.practice.searcher.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Created by Станислав on 08.11.2016.
 */
public class ServerHandler extends AbstractHandler {
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(doSearch(request));
    }

    private String doSearch(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.equals("set_example")) {
            String funcName = request.getParameter("func");

        }
        else if (uri.equals("get_example")) {

        }
        return "";
    }
}
