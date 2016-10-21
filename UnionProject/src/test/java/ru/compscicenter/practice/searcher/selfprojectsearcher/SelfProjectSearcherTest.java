package ru.compscicenter.practice.searcher.selfprojectsearcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Станислав on 08.10.2016.
 */
public class SelfProjectSearcherTest {

    SelfProjectSearcher searcher;

    @Before
    public void createSearcher() {
        searcher = new SelfProjectSearcher();
    }

    @Test
    public void testEmptyFile() {
//        System.out.println(searcher.search("fun", "../UnionProject/src/main/resources/EmptyFile.c"));
        assertEquals("<!DOCTYPE html><br><html><br><h3>Examples of this method usage from projects:</h3><br><body></body></html>",
                searcher.search("fun", "../UnionProject/src/main/resources/EmptyFile.c"));
//        assertEquals("", searcher.search("fun", "../UnionProject/src/main/resources/EmptyFile.c"));
    }
}
