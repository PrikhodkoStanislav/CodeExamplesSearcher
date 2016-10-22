package ru.compscicenter.practice.searcher.testsystem;

import org.junit.Before;
import org.junit.Test;
import ru.compscicenter.practice.searcher.codeduplicateremover.CodeDuplicateRemover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Станислав on 22.10.2016.
 */
public class TestSystem {

    private int failsFirstType = 0;
    private int failsSecondType = 0;
    private int allCompares = 0;

    private double errorFirstType() {
        double result = 0.0;
        if (allCompares > 0) {
            result = failsFirstType* 100 / allCompares;
        }
        return result;
    }

    private double errorSecondType() {
        double result = 0.0;
        if (allCompares > 0) {
            result = failsSecondType * 100 / allCompares;
        }
        return result;
    }

    private final String pathToGoldFile = "../UnionProject/src/test/java/resources/GoldFile";

    CodeDuplicateRemover codeDuplicateRemover;

    @Before
    public void createCodeDuplicateRemover() {
        codeDuplicateRemover = new CodeDuplicateRemover();
    }

    @Test
    public void testCodeDuplicateRemover() {
        try {
            FileReader fileReader = new FileReader(pathToGoldFile);
            BufferedReader in = new BufferedReader(fileReader);
            List<String[]> similarFunctions = new ArrayList<>();
            String str;
            while ((str = in.readLine()) != null) {
                String[] strs = str.split(" ");
                similarFunctions.add(strs);
            }
            // Compare files which must be the same and calculate the error of the first type.
            for (String[] strs : similarFunctions) {
                int length = strs.length;
                for (int i = 0; i < length; i++) {
                    for (int j = i + 1; j < length; j++) {
                        String pathtoFile1 = "../UnionProject/src/main/resources/" + strs[i];
                        String pathtoFile2 = "../UnionProject/src/main/resources/" + strs[j];
                        if (!codeDuplicateRemover.compareFunctionsFromFiles(pathtoFile1, pathtoFile2)) {
                            failsFirstType++;
                        }
                        allCompares++;
                    }
                }
            }
            System.out.println("First type errors: " + errorFirstType() + "%");
            System.out.println("Second type errors: " + errorSecondType() + "%");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
