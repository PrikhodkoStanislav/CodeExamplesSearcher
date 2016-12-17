package ru.compscicenter.practice.searcher;

import antlrclasses.CLexer;
import org.antlr.v4.runtime.*;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.algorithms.AlgorithmsRemoveDuplicates;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.IOException;
import java.util.*;

/**
 * Created by Станислав on 15.10.2016!
 */
public class CodeDuplicateRemover {
    private final static Logger logger = Logger.getLogger(CodeDuplicateRemover.class);

    private List<CodeExample> list;
    private AlgorithmsRemoveDuplicates typeOfRemoveAlgorithm = AlgorithmsRemoveDuplicates.EqualsTokens;
    private final double maxLevenshteinRatio = 30.0;

    public CodeDuplicateRemover(AlgorithmsRemoveDuplicates typeOfRemoveAlgorithm) {
        this.typeOfRemoveAlgorithm = typeOfRemoveAlgorithm;
    }

    public CodeDuplicateRemover(List<CodeExample> list, AlgorithmsRemoveDuplicates typeOfRemoveAlgorithm) {
        this(typeOfRemoveAlgorithm);
        this.list = list;
    }

    public List<CodeExample> removeDuplicates() {
        int numberOfDuplicates = 0;
        List<CodeExample> examples = new ArrayList<>(list);
        List<CodeExample> result = new ArrayList<>();
        while (!examples.isEmpty()) {
            CodeExample minCodeExample = examples.stream().min(
                    Comparator.comparing(this::tokenLength)).get();
            examples.remove(minCodeExample);
            List<CodeExample> removedElements = new ArrayList<>();
            for (CodeExample ce : examples) {
                if (compareCodeExamples(minCodeExample, ce)) {
                    removedElements.add(ce);
                }
            }
            numberOfDuplicates += removedElements.size();
            examples.removeAll(removedElements);
            result.add(minCodeExample);
        }
        logger.info("Number of duplicates = " + numberOfDuplicates);
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
        list = result;
        return result;
    }

    private Integer tokenLength(CodeExample codeExample1) {
        Lexer lexer = new CLexer(new ANTLRInputStream(codeExample1.getCodeExample()));
        List<? extends Token> str = lexer.getAllTokens();
        return str.size();
    }

    public boolean compareFunctionsFromFiles(String fileName1, String fileName2) throws IOException {
        Lexer lexer1 = new CLexer(new ANTLRFileStream(fileName1));
        Lexer lexer2 = new CLexer(new ANTLRFileStream(fileName2));

        return lexerAnalysis(lexer1, lexer2);
    }

    private boolean compareCodeExamples(CodeExample codeExample1, CodeExample codeExample2) {
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

        switch (typeOfRemoveAlgorithm) {
            case EqualsTokens:
                result = listEquals(arr1, arr2);
                break;
            case LevenshteinDistance:
                int ld = levenshteinDistance(arr1, arr2);
                int maxLength = Math.max(arr1.size(), arr2.size());
                double levenshteinRatio = (double)ld * 100 / maxLength;
                result = (levenshteinRatio - maxLevenshteinRatio <= 0.05);
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

        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;

            for(int i = 1; i < len0; i++) {
                int match = (lhs.get(i - 1).equals(rhs.get(j - 1))) ? 0 : 1;

                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost; cost = newcost; newcost = swap;
        }
        return cost[len0 - 1];
    }
}
