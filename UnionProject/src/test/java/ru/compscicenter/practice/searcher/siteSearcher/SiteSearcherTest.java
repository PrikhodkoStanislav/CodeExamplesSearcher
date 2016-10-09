package ru.compscicenter.practice.searcher.siteSearcher;

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
        assertEquals("http://en.cppreference.com/w/c/numeric/math/sin",
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
        assertEquals("http://en.cppreference.com/w/c/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen"));
    }

    @Test
    public void testCppStrlenS() {
        assertEquals("http://www.cplusplus.com/reference/cstring/strlen",
                testCorrectURL(cppProcessor, "strlen_s"));
    }

    @Test
    public void testCppRefStrlenS() {
        assertEquals("http://en.cppreference.com/w/c/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen_s"));
    }

    @Test
    public void testCppErrorlenS() {
        assertEquals("http://www.cplusplus.com/reference/cstring/strerror",
                testCorrectURL(cppProcessor, "strerrorlen_s"));
    }

    @Test
    public void testCppRefErrorlenS() {
        assertEquals("http://en.cppreference.com/w/c/string/byte/strerror",
                testCorrectURL(cppReferenceProcessor, "strerrorlen_s"));
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
        assertEquals("http://en.cppreference.com/w/c/string/byte/atoi",
                testCorrectURL(cppReferenceProcessor, "std::atoi"));
    }

    @Test
    public void testCppAtoI() {
        assertEquals("http://www.cplusplus.com/reference/cstdlib/atoi/",
                testCorrectURL(cppProcessor, "std::atoi"));
    }

    @Test
    public void testCppRefAtoLL() {
        assertEquals("http://en.cppreference.com/w/c/string/byte/atoi",
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

    @Test
    public void testCppRefWScanf() {
        assertEquals("http://en.cppreference.com/w/cpp/io/c/fwscanf",
                testCorrectURL(cppReferenceProcessor, "std::wscanf"));
    }

    @Test
    public void testCppWScanf() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/wscanf/",
                testCorrectURL(cppProcessor, "std::wscanf"));
    }

    @Test
    public void testCppRefIsAlpha() {
        assertEquals("http://en.cppreference.com/w/c/string/byte/isalpha",
                testCorrectURL(cppReferenceProcessor, "std::isalpha"));
    }

    @Test
    public void testCppIsAlpha() {
        assertEquals("http://www.cplusplus.com/reference/cctype/isalpha/",
                testCorrectURL(cppProcessor, "std::isalpha"));
    }

    public String testCorrectURL(SiteProcessor processor, String query) {
        return processor.generateRequestURL(query);
    }
}
