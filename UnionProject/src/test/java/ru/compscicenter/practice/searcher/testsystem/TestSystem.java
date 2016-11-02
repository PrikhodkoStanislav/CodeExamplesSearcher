package ru.compscicenter.practice.searcher.testsystem;

import org.junit.Before;
import org.junit.Test;
import ru.compscicenter.practice.searcher.CodeDuplicateRemover;
import ru.compscicenter.practice.searcher.algorithms.AlgorithmsRemoveDuplicates;

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
    CodeDuplicateRemover codeDuplicateRemover1;
    CodeDuplicateRemover codeDuplicateRemover2;

    @Before
    public void createCodeDuplicateRemover() {
        codeDuplicateRemover1 = new CodeDuplicateRemover(AlgorithmsRemoveDuplicates.EqualsTokens);
        codeDuplicateRemover2 = new CodeDuplicateRemover(AlgorithmsRemoveDuplicates.LevenshteinDistance);
    }

    public void testCompareFunctionsFromFiles(CodeDuplicateRemover codeDuplicateRemover) {
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
                for (int i = 0; i < (length - 1); i++) {
                    String firstFileName = strs[i];
                    String pathToFile1 = "../UnionProject/src/main/resources/" + firstFileName;
                    // Compare files which must be the same and calculate the error of the first type.
                    for (int j = i + 1; j < length; j++) {
                        String secondFileName = strs[j];
                        String pathToFile2 = "../UnionProject/src/main/resources/" + secondFileName;
                        if (!codeDuplicateRemover.compareFunctionsFromFiles(pathToFile1, pathToFile2)) {
                            failsFirstType++;
                        }
                        allFirstTypeCompares++;
                    }
                    // Compare files which must be the different and calculate the error of the second type.
                    List<String> differentFiles = new ArrayList<>(allFileNames);
                    differentFiles.removeAll(Arrays.asList(strs));
                    for (String differentFileName : differentFiles) {
                        String pathToFile2 = "../UnionProject/src/main/resources/" + differentFileName;
                        if (codeDuplicateRemover.compareFunctionsFromFiles(pathToFile1, pathToFile2)) {
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

    @Test
    public void testCodeDuplicateRemoverWithEqualsList() {
        System.out.println("CodeDuplicateRemover with algorithmsRemoveDuplicates = EqualsTokens");
        testCompareFunctionsFromFiles(codeDuplicateRemover1);
    }

    @Test
    public void testCodeDuplicateRemoverWithLevenshteinDistance() {
        System.out.println("CodeDuplicateRemover with algorithmsRemoveDuplicates = LevenshteinDistance " +
                "and maxLevenshteinRatio = 30%");
        testCompareFunctionsFromFiles(codeDuplicateRemover2);
    }
}
