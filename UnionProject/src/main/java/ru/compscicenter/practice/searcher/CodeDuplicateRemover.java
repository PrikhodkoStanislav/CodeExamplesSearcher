package ru.compscicenter.practice.searcher;

import antlrclasses.CLexer;
import org.antlr.v4.runtime.*;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.io.IOException;
import java.util.*;

/**
 * Created by Станислав on 15.10.2016!
 */
public class CodeDuplicateRemover {
    private List<CodeExample> list;
    private int typeOfCompareExamples = 1;

    public CodeDuplicateRemover(int typeOfCompareExamples) {
        this.typeOfCompareExamples = typeOfCompareExamples;
    }

    public CodeDuplicateRemover(List<CodeExample> list, int typeOfCompareExamples) {
        this.list = list;
        this.typeOfCompareExamples = typeOfCompareExamples;
    }

    public List<CodeExample> removeDuplicates() {
        List<CodeExample> result = new ArrayList<>(list);
        int sizeOfList = result.size();
        for (int i = 0; i < sizeOfList - 1; i++) {
            for (int j = i + 1; j < sizeOfList; j++) {
                CodeExample ce1 = result.get(i);
                CodeExample ce2 = result.get(j);
                if (compareCodeExamples(ce1, ce2)) {
                    result.remove(ce2);
                }
            }
        }
//        Map<CodeExample, Lexer> lexers = new HashMap<>();
//        Map<CodeExample, List<Integer>> tokenTypes = new HashMap<>();
//        for (CodeExample ce : list) {
//            lexers.put(ce, new CLexer(new ANTLRInputStream(ce.getCodeExample())));
//            List<? extends Token> tokens = lexers.get(ce).getAllTokens();
//            List<Integer> typesOfTokens = new ArrayList<>();
//            for (Token t : tokens) {
//                typesOfTokens.add(t.getType());
//            }
//            tokenTypes.put(ce, typesOfTokens);
//        }
//        deleteDuplicates(tokenTypes);
        return result;
    }

    public boolean compareFunctionsFromFiles(String fileName1, String fileName2) throws IOException {
        Lexer lexer1 = new CLexer(new ANTLRFileStream(fileName1));
        Lexer lexer2 = new CLexer(new ANTLRFileStream(fileName2));

        return lexerAnalysis(lexer1, lexer2);
    }

    public boolean compareCodeExamples(CodeExample codeExample1, CodeExample codeExample2) {
        Lexer lexer1 = new CLexer(new ANTLRInputStream(codeExample1.getCodeExample()));
        Lexer lexer2 = new CLexer(new ANTLRInputStream(codeExample2.getCodeExample()));

        return lexerAnalysis(lexer1, lexer2);
    }

    private boolean lexerAnalysis(Lexer lexer1, Lexer lexer2) {
        List<? extends Token> str1 = lexer1.getAllTokens();
        List<? extends Token> str2 = lexer2.getAllTokens();

        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();

        for (Token s : str1) {
            arr1.add(s.getType());
        }
        for (Token s : str2) {
            arr2.add(s.getType());
        }

        boolean result = false;

        switch (typeOfCompareExamples) {
            case (1):
                result = listEquals(arr1, arr2);
                break;
            case (2):
                int ld = levenshteinDistance(arr1, arr2);
                final int maxForLevenshteinDistance = 10;
                result = (ld <= maxForLevenshteinDistance);
                break;
            default:
                break;
        }

        return result;
    }

    private boolean listEquals(List<Integer> arr1, List<Integer> arr2) {
        return arr1.equals(arr2);
    }

    private int levenshteinDistance(List<Integer> lhs, List<Integer> rhs) {
        int len0 = lhs.size() + 1;
        int len1 = rhs.size() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.get(i - 1).equals(rhs.get(j - 1))) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}
