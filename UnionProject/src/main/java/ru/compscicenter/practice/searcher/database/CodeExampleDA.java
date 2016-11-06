package ru.compscicenter.practice.searcher.database;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 04.11.2016!
 */
public class CodeExampleDA {
    private final static Logger logger = Logger.getLogger(CodeExampleDA.class);

    private static CodeExampleDA instance;

    private PrimaryIndex<String, CodeExample> primaryIndex;
    private SecondaryIndex<String, String, CodeExample> secondaryIndexByLanguage;
    private SecondaryIndex<String, String, CodeExample> secondaryIndexByFunction;
    private SecondaryIndex<String, String, CodeExample> secondaryIndexBySource;

    private DatabaseConfig dbConfig;

    private CodeExampleDA() {
        logger.setLevel(Level.INFO);

        dbConfig = DatabaseConfig.getInstance();

        primaryIndex = dbConfig.getStore().getPrimaryIndex(
                String.class, CodeExample.class);
        secondaryIndexByLanguage = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "language");
        secondaryIndexByFunction = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "function");
        secondaryIndexBySource = dbConfig.getStore().getSecondaryIndex(
                primaryIndex, String.class, "source");
    }

    public static CodeExampleDA getInstance() {
        if (instance == null)
            instance = new CodeExampleDA();
        return instance;
    }

    public void save(CodeExample entity) {
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

    public CodeExample loadByExample(String example) {
        return primaryIndex.get(example);
    }

    public List<CodeExample> loadByLanguage(String language) {
        List<CodeExample> result = new LinkedList<>();
        EntityCursor<CodeExample> cursor = secondaryIndexByLanguage.subIndex(language).entities();;
        for (CodeExample entity : cursor) {
            result.add(entity);
        }
        cursor.close();
        return result;
    }

    public List<CodeExample> loadByFunction(String function) {
        List<CodeExample> result = new LinkedList<>();
        EntityCursor<CodeExample> cursor = secondaryIndexByFunction.subIndex(function).entities();;
        for (CodeExample entity : cursor) {
            result.add(entity);
        }
        cursor.close();
        return result;
    }

    public List<CodeExample> loadByLanguageAndFunction(String language, String function) {
        List<CodeExample> result = new LinkedList<>();
        ForwardCursor<CodeExample> entities = findEntitiesByLanguageAndFunction(
                primaryIndex,
                secondaryIndexByLanguage, language,
                secondaryIndexByFunction, function);
        for (CodeExample entity : entities) {
            result.add(entity);
        }
        entities.close();
        return result;
    }

    /**
     * SELECT * FROM examples
     * WHERE language = 'key1' AND function = key2;
     */
    private ForwardCursor<CodeExample> findEntitiesByLanguageAndFunction (
                        PrimaryIndex<String, CodeExample> pk,
                        SecondaryIndex<String, String, CodeExample> sk1,
                        String key1,
                        SecondaryIndex<String, String, CodeExample> sk2,
                        String key2)
            throws DatabaseException {
        assert (pk != null);
        assert (sk1 != null);
        assert (sk2 != null);
        EntityJoin<String, CodeExample> join = new EntityJoin<>(pk);
        join.addCondition(sk1, key1);
        join.addCondition(sk2, key2);
        return join.entities();
    }

    public List<CodeExample> loadByLanguageFunctionAndSource(String language, String function, String source) {
        List<CodeExample> result = new LinkedList<>();
        ForwardCursor<CodeExample> entities = findEntitiesByLanguageFunctionSourceAndExample(
                primaryIndex,
                secondaryIndexByLanguage, language,
                secondaryIndexByFunction, function,
                secondaryIndexBySource, source);
        for (CodeExample entity : entities) {
            result.add(entity);
        }
        entities.close();
        return result;
    }

    /**
     * SELECT * FROM examples
     * WHERE language = 'key1' AND function = key2;
     */
    private ForwardCursor<CodeExample> findEntitiesByLanguageFunctionSourceAndExample (
            PrimaryIndex<String, CodeExample> pk,
            SecondaryIndex<String, String, CodeExample> sk1,
            String key1,
            SecondaryIndex<String, String, CodeExample> sk2,
            String key2,
            SecondaryIndex<String, String, CodeExample> sk3,
            String key3)
            throws DatabaseException {
        assert (pk != null);
        assert (sk1 != null);
        assert (sk2 != null);
        assert (sk3!= null);
        EntityJoin<String, CodeExample> join = new EntityJoin<>(pk);
        join.addCondition(sk1, key1);
        join.addCondition(sk2, key2);
        join.addCondition(sk3, key3);
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

    public void updateDB(List<CodeExample> examples) {
        for (CodeExample codeExample : examples) {
            List<CodeExample> ce = loadByLanguageFunctionAndSource(
                    codeExample.getLanguage(),
                    codeExample.getFunction(),
                    codeExample.getSource());
            if (ce != null) {
                CodeExample example = ce.get(0);
                if (example == null) {
                    save(codeExample);
                } else if (example.getCodeExample().length() >
                        codeExample.getCodeExample().length()) {
                    primaryIndex.put(codeExample);
                }
            }
        }
    }
}
