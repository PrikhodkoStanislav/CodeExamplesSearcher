package ru.compscicenter.practice.searcher.server;

import org.eclipse.jetty.server.Server;

/**
 * Created by Станислав on 09.11.2016.
 */
public class CreateServer {
    public CreateServer() {
        Server server = new Server(8080);
        server.setHandler(new ServerHandler());
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
