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

    public CodeDuplicateRemover() {}

    public CodeDuplicateRemover(List<CodeExample> list) {
        this.list = list;
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
        boolean result = arr1.equals(arr2);
        return result;
    }

//    public void deleteDuplicates(Map<CodeExample, List<Integer>> tokenTypes) {
//    }

    public List<CodeExample> removeDuplicates() {
        List<CodeExample> result = new ArrayList<>(list);
        CodeExample[] arrayOfCodeExamples = (CodeExample[]) result.toArray();
        int sizeOfList = arrayOfCodeExamples.length;
        for (int i = 0; i < sizeOfList - 1; i++) {
            for (int j = i + 1; j < sizeOfList; j++) {
                CodeExample ce1 = arrayOfCodeExamples[i];
                CodeExample ce2 = arrayOfCodeExamples[j];
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
}
