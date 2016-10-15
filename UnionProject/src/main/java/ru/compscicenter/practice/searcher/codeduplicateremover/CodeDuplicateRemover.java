package ru.compscicenter.practice.searcher.codeduplicateremover;

import antlrclasses.CLexer;
import org.antlr.v4.runtime.*;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.*;

/**
 * Created by Станислав on 15.10.2016.
 */
public class CodeDuplicateRemover {
    private List<CodeExample> list;

    public CodeDuplicateRemover(List<CodeExample> list) {
        this.list = list;
    }

    public void deleteDuplicates(Map<CodeExample, List<Integer>> tokenTypes) {

    }

    public List<CodeExample> removeDuplicates() {
        List<Lexer> lexers = new ArrayList<>();
        Map<CodeExample, List<Integer>> tokenTypes = new HashMap<>();
        int i = 0;
        for (CodeExample ce : list) {
            lexers.set(i, new CLexer(new ANTLRInputStream(ce.codeExample)));
            List<? extends Token> tokens = lexers.get(i).getAllTokens();
            List<Integer> typesOfTokens = new ArrayList<>();
            for (Token t : tokens) {
                typesOfTokens.add(t.getType());
            }
            tokenTypes.put(ce, typesOfTokens);
            i++;
        }
        deleteDuplicates(tokenTypes);
        return list;
//        List<Integer> arr1 = new ArrayList<>();
//        List<Integer> arr2 = new ArrayList<>();
//        List<Integer> arr3 = new ArrayList<>();
//        for (Token s : str1) {
//            arr1.add(s.getType());
//        }
//        for (Token s : str2) {
//            arr2.add(s.getType());
//        }
//        for (Token s : str3) {
//            arr3.add(s.getType());
//        }
//        System.out.println(arr1.equals(arr2));
//        System.out.println(arr1.equals(arr3));
    }
}
