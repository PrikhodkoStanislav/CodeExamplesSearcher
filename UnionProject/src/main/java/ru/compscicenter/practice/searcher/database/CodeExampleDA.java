package ru.compscicenter.practice.searcher.database;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.xml.soap.SAAJResult;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 04.11.2016!
 */
public class CodeExampleDA {
    private final static Logger logger = Logger.getLogger(CodeExampleDA.class);

    private PrimaryIndex<String, CodeExampleEntity> primaryIndex;
    private SecondaryIndex<String, String, CodeExampleEntity> secondaryIndexByLanguage;
    private SecondaryIndex<String, String, CodeExampleEntity> secondaryIndexByFunction;

    private DatabaseConfig dbConfig;

    public CodeExampleDA() {
        logger.setLevel(Level.INFO);

        dbConfig = DatabaseConfig.getInstance();

        primaryIndex = dbConfig.getStore().getPrimaryIndex(
                String.class, CodeExampleEntity.class);
        secondaryIndexByLanguage = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "language");
        secondaryIndexByFunction = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "function");
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
                    "function=" + entity.getFunction() +
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
        EntityCursor<CodeExampleEntity> cursor = secondaryIndexByLanguage.subIndex(language).entities();;
        for (CodeExampleEntity entity : cursor) {
            result.add(entity);
        }
        cursor.close();
        return result;
    }

    public List<CodeExampleEntity> loadByFunction(String function) {
        List<CodeExampleEntity> result = new LinkedList<>();
        EntityCursor<CodeExampleEntity> cursor = secondaryIndexByFunction.subIndex(function).entities();;
        for (CodeExampleEntity entity : cursor) {
            result.add(entity);
        }
        cursor.close();
        return result;
    }

    public List<CodeExampleEntity> loadByLanguageAndFunction(String language, String function) {
        List<CodeExampleEntity> result = new LinkedList<>();
        ForwardCursor<CodeExampleEntity> entities = findEntitiesByLanguageAndFunction(
                primaryIndex,
                secondaryIndexByLanguage, language,
                secondaryIndexByFunction, function);
        for (CodeExampleEntity entity : entities) {
            result.add(entity);
        }
        entities.close();
        return result;
    }

    /**
     * SELECT * FROM examples
     * WHERE language = 'key1' AND function = key2;
     */
    public ForwardCursor<CodeExampleEntity> findEntitiesByLanguageAndFunction (
                        PrimaryIndex<String, CodeExampleEntity> pk,
                        SecondaryIndex<String, String, CodeExampleEntity> sk1,
                        String key1,
                        SecondaryIndex<String, String, CodeExampleEntity> sk2,
                        String key2)
            throws DatabaseException {
        assert (pk != null);
        assert (sk1 != null);
        assert (sk2 != null);
        EntityJoin<String, CodeExampleEntity> join = new EntityJoin<>(pk);
        join.addCondition(sk1, key1);
        join.addCondition(sk2, key2);
        return join.entities();
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
