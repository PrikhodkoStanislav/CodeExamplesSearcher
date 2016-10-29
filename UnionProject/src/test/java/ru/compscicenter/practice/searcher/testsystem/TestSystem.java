package ru.compscicenter.practice.searcher.testsystem;

import org.junit.Before;
import org.junit.Test;
import ru.compscicenter.practice.searcher.CodeDuplicateRemover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Станислав on 22.10.2016.
 */
public class TestSystem {

    private int failsFirstType = 0;
    private int allFirstTypeCompares = 0;
    private int failsSecondType = 0;
    private int allSecondTypeCompares = 0;

    private double errorFirstType() {
        double result = 0.0;
        if (allFirstTypeCompares > 0) {
            result = failsFirstType* 100 / allFirstTypeCompares;
        }
        return result;
    }

    private double errorSecondType() {
        double result = 0.0;
        if (allSecondTypeCompares > 0) {
            result = failsSecondType * 100 / allSecondTypeCompares;
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
            List<String[]> fileNamesOfSimilarFunctions = new ArrayList<>();
            List<String> allFileNames = new ArrayList<>();
            String str;
            while ((str = in.readLine()) != null) {
                String[] strs = str.split(" ");
                fileNamesOfSimilarFunctions.add(strs);
                allFileNames.addAll(Arrays.asList(strs));
            }
            for (String[] strs : fileNamesOfSimilarFunctions) {
                int length = strs.length;
                for (int i = 0; i < length; i++) {
                    String firstFileName = strs[i];
                    String pathtoFile1 = "../UnionProject/src/main/resources/" + firstFileName;
                    List<String> differentFiles = allFileNames;
                    differentFiles.remove(firstFileName);
                    // Compare files which must be the same and calculate the error of the first type.
                    for (int j = i + 1; j < length; j++) {
                        String secondFileName = strs[j];
                        String pathToFile2 = "../UnionProject/src/main/resources/" + secondFileName;
                        differentFiles.remove(secondFileName);
                        if (!codeDuplicateRemover.compareFunctionsFromFiles(pathtoFile1, pathToFile2)) {
                            failsFirstType++;
                        }
                        allFirstTypeCompares++;
                    }
                    // Compare files which must be the different and calculate the error of the second type.
                    for (String differentFileName : differentFiles) {
                        String pathToFile2 = "../UnionProject/src/main/resources/" + differentFileName;
                        if (codeDuplicateRemover.compareFunctionsFromFiles(pathtoFile1, pathToFile2)) {
                            failsSecondType++;
                        }
                        allSecondTypeCompares++;
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
