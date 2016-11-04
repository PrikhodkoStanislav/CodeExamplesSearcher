package ru.compscicenter.practice.searcher.database;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 04.11.2016!
 */
public class CodeExampleDA {
    private final static Logger logger = Logger.getLogger(CodeExampleDA.class);

    private PrimaryIndex<String, CodeExampleEntity> primaryIndex;
    private SecondaryIndex<String, String, CodeExampleEntity> secondaryIndex;

    private DatabaseConfig dbConfig;

    public CodeExampleDA() {
        logger.setLevel(Level.INFO);

        dbConfig = DatabaseConfig.getInstance();

        primaryIndex = dbConfig.getStore().getPrimaryIndex(
                String.class, CodeExampleEntity.class);
        secondaryIndex = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "language");
    }

    public void save(CodeExampleEntity entity) {
        Transaction tx = dbConfig.startTransaction();
        try {
            /*if (entity.getId() == 0) {
                long id = dbConfig.getStore().getSequence("SPENT_ID").get(tx, 1);
                entity.setId(id);
            }*/
            primaryIndex.put(tx, entity);
            logger.info("Add new data to database: CodeExampleEntity {" +
                    "language=" + entity.getLanguage() +
                    "function=" + entity.getFunctionName() +
                    "source=" + entity.getSource());
            tx.commit();
        } catch (Exception e) {
            logger.error("Sorry, something wrong!", e);
            if (tx != null) {
                tx.abort();
                tx = null;
            }
        }
    }

    public CodeExampleEntity loadByExample(String example) {
        return primaryIndex.get(example);
    }
    public List<CodeExampleEntity> loadByLanguage(String language) {
        List<CodeExampleEntity> result = new LinkedList<>();
        EntityCursor<CodeExampleEntity> cursor = secondaryIndex.subIndex(language).entities();;
        for (CodeExampleEntity entity : cursor) {
            result.add(entity);
        }
        cursor.close();
        return result;
    }


    public void removeCodeExampleEntity(String example) {
        try {
            primaryIndex.delete(example);
        } catch (DatabaseException e) {
            logger.error("Sorry, something wrong!", e);
            primaryIndex.delete(example);
        }
    }
}
