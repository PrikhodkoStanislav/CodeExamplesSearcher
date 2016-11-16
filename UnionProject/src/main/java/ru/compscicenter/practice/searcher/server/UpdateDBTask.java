package ru.compscicenter.practice.searcher.server;

import ru.compscicenter.practice.searcher.database.CodeExample;
import ru.compscicenter.practice.searcher.database.CodeExampleDA;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by user on 16.11.2016!
 */
public class UpdateDBTask extends TimerTask {
    private final static CodeExampleDA DATABASE = CodeExampleDA.getInstance();

    @Override
    public void run() {
        List<CodeExample> data = DATABASE.loadAllEntities();
        for (CodeExample example : data) {
            if (example.getModificationDate() < 0/*db-timestamp*/) {
                DATABASE.removeCodeExampleEntity(example.getId());
            }
        }
    }
}
