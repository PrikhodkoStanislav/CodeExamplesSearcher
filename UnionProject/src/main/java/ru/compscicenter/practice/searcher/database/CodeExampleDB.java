package ru.compscicenter.practice.searcher.database;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by user on 27.10.2016!
 */
public class CodeExampleDB {

    private static File examplesFile = new File("./examples.txt");

    // DatabaseEntries used for loading records
    private static DatabaseEntry theKey = new DatabaseEntry();
    private static DatabaseEntry theData = new DatabaseEntry();

    // Encapsulates the databases.
    private static MyDbs myDbs = new MyDbs();

    public static void main(String args[]) {
        CodeExampleDB edl = new CodeExampleDB();
        try {
            edl.run();
        } catch (DatabaseException dbe) {
            System.err.println("ExampleDatabaseLoad: " + dbe.toString());
            dbe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            e.printStackTrace();
        } finally {
            myDbs.close();
        }
        System.out.println("All done.");
    }


    private void run() throws DatabaseException {
        String myDbsPath = "./";
        myDbs.setup(myDbsPath);

        System.out.println("loading code examples db....");
        loadCodeExampleDb();
    }


    private void loadCodeExampleDb()
            throws DatabaseException {

        // loadFile opens a flat-text file that contains our data
        // and loads it into a list for us to work with. The integer
        // parameter represents the number of fields expected in the
        // file.
        List examples = loadFile(examplesFile, 8);

        // Now load the data into the database. The vendor's name is the
        // key, and the data is a Vendor class object.

        // Need a serial binding for the data
        TupleBinding<CodeExampleEntity> dataBinding = new CodeExampleBinding();

        for (Object example1 : examples) {
            String[] sArray = (String[]) example1;
            CodeExampleEntity codeExample = new CodeExampleEntity();
            codeExample.setLanguage(sArray[0]);
            codeExample.setFunctionName(sArray[1]);
            codeExample.setSource(sArray[2]);
            codeExample.setExample(sArray[3]);

            // The key is the vendor's name.
            // ASSUMES THE VENDOR'S NAME IS UNIQUE!
            String example = codeExample.getExample();
            try {
                theKey = new DatabaseEntry(example.getBytes("UTF-8"));
            } catch (IOException willNeverOccur) {
                willNeverOccur.printStackTrace();
            }

            // Convert the Vendor object to a DatabaseEntry object
            // using our SerialBinding
            dataBinding.objectToEntry(codeExample, theData);

            // Put it in the database.
            myDbs.getCodeExampleDB().put(null, theKey, theData);
        }
    }

    private List loadFile(File theFile, int numFields) {
        List<String[]> records = new ArrayList<>();
        try {
            String theLine;
            FileInputStream fis = new FileInputStream(theFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while((theLine=br.readLine()) != null) {
                String[] theLineArray = splitString(theLine, "#");
                if (theLineArray.length != numFields) {
                    System.out.println("Malformed line found in " + theFile.getPath());
                    System.out.println("Line was: '" + theLine);
                    System.out.println("length found was: " + theLineArray.length);
                    System.exit(-1);
                }
                records.add(theLineArray);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(theFile.getPath() + " does not exist.");
            e.printStackTrace();
        } catch (IOException e)  {
            System.err.println("IO Exception: " + e.toString());
            e.printStackTrace();
            System.exit(-1);
        }
        return records;
    }


    private static String[] splitString(String s, String delimiter) {
        List<String> resultList = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(s, delimiter);
        while (tokenizer.hasMoreTokens())
            resultList.add(tokenizer.nextToken());
        return (String[]) resultList.toArray();
    }

    protected CodeExampleDB() {}

}
