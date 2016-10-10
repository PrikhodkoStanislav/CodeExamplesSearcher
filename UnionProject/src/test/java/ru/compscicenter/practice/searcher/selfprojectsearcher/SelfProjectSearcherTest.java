package ru.compscicenter.practice.searcher.selfProjectSearcher;

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
        assertEquals("", searcher.search("fun", "../UnionProject/src/main/resources/EmptyFile.txt"));
    }
}
