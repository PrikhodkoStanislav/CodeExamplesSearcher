package ru.compscicenter.practice.searcher.server;

import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.database.CodeExample;
import ru.compscicenter.practice.searcher.database.CodeExampleDA;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by user on 16.11.2016!
 */
public class UpdateDBTask extends TimerTask {
    private final static Logger logger = Logger.getLogger(UpdateDBTask.class);

    private final static CodeExampleDA DATABASE = CodeExampleDA.getInstance();
    private final long defaultTimeout = 10000;

    @Override
    public void run() {
        long timeout = defaultTimeout;
        List<CodeExample> data = DATABASE.loadAllEntities();
        for (CodeExample example : data) {
            if (System.currentTimeMillis() - example.getModificationDate() >= timeout) {
                DATABASE.removeCodeExampleEntity(example.getId());
                logger.info("Code example with parameters: " +
                        "programming lang=" + example.getLanguage() + " " +
                        ", function=" + example.getFunction() + " " +
                        ", source=" + example.getSource() + " " +
                        " has expried!");
            }
        }
    }
}
