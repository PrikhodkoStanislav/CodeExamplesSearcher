package ru.compscicenter.practice.searcher.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;

/**
 * Created by Станислав on 09.11.2016.
 */
public class CreateServer {
    private final static Logger logger = Logger.getLogger(SelfProjectSearcher.class);

    public static void startServer() {
        logger.setLevel(Level.INFO);

        Server server = new Server(8080);
        server.setHandler(new ServerHandler());
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error("Sorry, something wrong!", e);
//            e.printStackTrace();
        }
    }
}
