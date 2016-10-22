package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public interface Searcher {
    public List<CodeExample> search(String functionName);
}
