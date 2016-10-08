package ru.compscicenter.practice.searcher.sitesearcher;

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
        assertEquals("http://www.cplusplus.com/reference/cstring/strlen",
                testCorrectURL(cppProcessor, "strlen"));
    }

    @Test
    public void testCppRefStrlen() {
        assertEquals("http://en.cppreference.com/w/cpp/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen"));
    }

    @Test
    public void testCppRefNthElement() {
        assertEquals("http://en.cppreference.com/w/cpp/algorithm/nth_element",
                testCorrectURL(cppReferenceProcessor, "std::nth_element"));
    }

    @Test
    public void testCppNthElement() {
        assertEquals("http://www.cplusplus.com/reference/algorithm/nth_element/",
                testCorrectURL(cppProcessor, "std::nth_element"));
    }

    @Test
    public void testCppRefAtoI() {
        assertEquals("http://en.cppreference.com/w/cpp/string/byte/atoi",
                testCorrectURL(cppReferenceProcessor, "std::atoi"));
    }

    @Test
    public void testCppAtoI() {
        assertEquals("http://www.cplusplus.com/reference/cstdlib/atoi/",
                testCorrectURL(cppProcessor, "std::atoi"));
    }

    @Test
    public void testCppRefAtoLL() {
        assertEquals("http://en.cppreference.com/w/cpp/string/byte/atoi",
                testCorrectURL(cppReferenceProcessor, "std::atoll"));
    }

    @Test
    public void testCppAtoLL() {
        assertEquals("http://www.cplusplus.com/reference/cstdlib/atoll/",
                testCorrectURL(cppProcessor, "std::atoll"));
    }

    @Test
    public void testCppRefScanf() {
        assertEquals("http://en.cppreference.com/w/cpp/io/c/fscanf",
                testCorrectURL(cppReferenceProcessor, "std::scanf"));
    }

    @Test
    public void testCppScanf() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/scanf/",
                testCorrectURL(cppProcessor, "std::scanf"));
    }

    @Test
    public void testCppRefFgetC() {
        assertEquals("http://en.cppreference.com/w/cpp/io/c/fgetc",
                testCorrectURL(cppReferenceProcessor, "std::getc"));
    }

    @Test
    public void testCppGetC() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/getc/",
                testCorrectURL(cppProcessor, "std::getc"));
    }

    @Test
    public void testCppFgetC() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/fgetc/",
                testCorrectURL(cppProcessor, "std::fgetc"));
    }

    public String testCorrectURL(SiteProcessor processor, String query) {
        return processor.generateRequestURL(query);
    }
}
