package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 13.10.2016!
 */
public class CodeExamplesStorage {

    private List<CodeExample> examples;

    private static CodeExamplesStorage instance;

    private CodeExamplesStorage() {
        examples = new ArrayList<>();
    }

    public static CodeExamplesStorage getInstance() {
        if (instance == null)
            instance = new CodeExamplesStorage();
        return instance;
    }

    public synchronized void addCodeExample(CodeExample example) {
        examples.add(example);
    }

    public List<CodeExample> getExamples() {
        return examples;
    }

    public void removeDuplicates() {}
}
