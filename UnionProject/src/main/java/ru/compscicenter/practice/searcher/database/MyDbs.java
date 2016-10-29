package ru.compscicenter.practice.searcher.database;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;

import java.io.FileNotFoundException;

/**
 * Created by user on 28.10.2016!
 */
public class MyDbs {
    private Database exampleDb = null;
    private Database classCatalogDb = null;

    // Needed for object serialization
    private StoredClassCatalog classCatalog;

    private String codeExampleDb = "CodeExampleDB.db";
    private String classcatalogdb = "ClassCatalogDB.db";

    // Our constructor does nothing
    public MyDbs() {}

    // The setup() method opens all our databases
    // for us.
    public void setup(String databasesHome)
            throws DatabaseException {

        DatabaseConfig myDbConfig = new DatabaseConfig();

        // Now open, or create and open, our databases
        // Open the vendors and inventory databases
        /*try {
            codeExampleDb = databasesHome + "/" + codeExampleDb;
            //codeExampleDb = new Database(codeExampleDb, null, myDbConfig);

            // Open the class catalog db. This is used to
            // optimize class serialization.
            classcatalogdb = databasesHome + "/" + classcatalogdb;
            //classCatalogDb = new Database(classcatalogdb, null, myDbConfig);

        } catch(FileNotFoundException fnfe) {
            System.err.println("MyDbs: " + fnfe.toString());
            System.exit(-1);
        }*/
    }

    // getter methods
    public Database getCodeExampleDB() {
        return exampleDb;
    }

    public StoredClassCatalog getClassCatalog() {
        return classCatalog;
    }

    // Close the databases
    public void close() {
        try {
            if (exampleDb != null) {
                exampleDb.close();
            }

            if (classCatalogDb != null) {
                classCatalogDb.close();
            }
        } catch(DatabaseException dbe) {
            System.err.println("Error closing MyDbs: " +
                    dbe.toString());
            System.exit(-1);
        }
    }
}
