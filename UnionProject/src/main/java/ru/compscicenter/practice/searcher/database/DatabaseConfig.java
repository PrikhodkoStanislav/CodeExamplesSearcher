package ru.compscicenter.practice.searcher.database;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.evolve.IncompatibleClassException;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 04.11.2016!
 */
public class DatabaseConfig {
    private static DatabaseConfig ourInstance;
    private final static String configPath = "." + File.separator + "JEDB";
    private static File envDir = new File(configPath);

    private Environment envmnt;
    private EntityStore store;

    public static DatabaseConfig getInstance() {
        if (ourInstance == null)
            ourInstance = new DatabaseConfig();
        return ourInstance;
    }

    public static void init() {
        ourInstance = new DatabaseConfig();
    }

    private DatabaseConfig() {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);

        if (!envDir.exists()) {
            envDir.mkdirs();
            try {
                envDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        envmnt = new Environment(envDir, envConfig);
        try {
            store = new EntityStore(envmnt, "codeexamples", storeConfig);
        } catch (IncompatibleClassException e) {
            //todo: реализовать преобразования данных.
        }
    }

    public static void shutdown() {
        if (ourInstance != null) {
            ourInstance.close();
        }
    }

    private void close() {
        store.close();
        envmnt.close();

    }

    public EntityStore getStore() {
        return store;
    }

    public Transaction startTransaction() {
        return envmnt.beginTransaction(null, null);
    }

}