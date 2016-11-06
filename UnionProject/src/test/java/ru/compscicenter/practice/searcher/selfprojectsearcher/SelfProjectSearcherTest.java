package ru.compscicenter.practice.searcher.selfprojectsearcher;

import org.junit.Test;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Станислав on 08.10.2016.
 */
public class SelfProjectSearcherTest {

    SelfProjectSearcher searcher;

    @Test
    public void testEmptyFile() {
        searcher = new SelfProjectSearcher("../UnionProject/src/main/resources/EmptyFile.c");
        assertEquals(new ArrayList<CodeExample>(), searcher.search("fun"));
//        System.out.println(searcher.search("fun"));
    }
}
