package ru.compscicenter.practice.searcher;

import com.sun.org.apache.bcel.internal.classfile.Code;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class Searcher {
    public List<CodeExample> search(String query) {
        return new ArrayList<>();
    }

    public String search(String query, String path) {
        return "No such method found by the path!";
    }
}
