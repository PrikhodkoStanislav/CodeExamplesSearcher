import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


import java.io.File;

public class TestBDB {

    public static void main(String[] args) {
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        try {
            // Open the environment, creating one if it does not exist
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            myDbEnvironment = new Environment(new File("./dbEnv"),
                                              envConfig);

            // Open the database, creating one if it does not exist
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            myDatabase = myDbEnvironment.openDatabase(null,
                                             "TestDatabase", dbConfig);

            String key = "myKey";
            DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
            if(args[0].equals("store")) {
                    String data = args[1];
                    DatabaseEntry theData = new DatabaseEntry(data.getBytes("UTF-8"));
                    myDatabase.put(null, theKey, theData);
            }
            else if(args[0].equals("read")) {
                DatabaseEntry theData = new DatabaseEntry();

                if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {

                    // Translate theData into a String.
                    byte[] retData = theData.getData();
                    String foundData = new String(retData, "UTF-8");
                    System.out.println("key: '" + key + "' data: '" +
                                        foundData + "'.");
                } else {
                    System.out.println("No record found with key '" + key + "'.");
                }
            }
            try {
                    if (myDatabase != null) {
                        myDatabase.close();
                    }
             
                    if (myDbEnvironment != null) {
                        myDbEnvironment.close();
                    }
            } catch (DatabaseException dbe) {
                // Exception handling
            }            
        } catch (Exception dbe) {
            dbe.printStackTrace();
            System.out.println(dbe);
        }

    }
}
