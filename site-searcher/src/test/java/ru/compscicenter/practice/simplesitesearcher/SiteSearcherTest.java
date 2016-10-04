package ru.compscicenter.practice.simplesitesearcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by user on 03.10.2016!
 */
public class SiteSearcherTest {

    SiteProcessor cppReferenceProcessor;
    SiteProcessor cppProcessor;

    @Before
    public void createSiteSearcher() {
        cppReferenceProcessor = new CPPReferenceSiteProcessor();
        cppProcessor = new CPlusPlusSiteProcessor();
    }

    @Test
    public void testCppRefSin() {
        assertEquals("http://en.cppreference.com/w/cpp/numeric/math/sin",
                testCorrectURL(cppReferenceProcessor, "sin"));
    }

    @Test
    public void testCppSin() {
        assertEquals("http://www.cplusplus.com/reference/cmath/sin",
                testCorrectURL(cppProcessor, "sin"));
    }

    @Test
    public void testResize() {
        assertEquals("http://www.cplusplus.com/reference/vector/vector/resize/",
                testCorrectURL(cppProcessor, "vector::resize"));
    }

    @Test
    public void testCppStrlen() {
        assertEquals("http://www.cplusplus.com/reference/cstring/strlen/",
                testCorrectURL(cppProcessor, "strlen"));
    }

    @Test
    public void testCppRefStrlen() {
        assertEquals("http://en.cppreference.com/w/cpp/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen"));
    }

    public String testCorrectURL(SiteProcessor processor, String query) {
        return processor.generateRequestURL(query);
    }
}
