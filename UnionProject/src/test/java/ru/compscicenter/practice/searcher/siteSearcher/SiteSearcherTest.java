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
        assertEquals("http://www.cplusplus.com/reference/cmath/sin/",
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
        assertEquals("http://en.cppreference.com/w/c/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen"));
    }

    @Test
    public void testCppStrlenS() {
        assertEquals("http://www.cplusplus.com/reference/cstring/strlen/",
                testCorrectURL(cppProcessor, "strlen_s"));
    }

    @Test
    public void testCppRefStrlenS() {
        assertEquals("http://en.cppreference.com/w/c/string/byte/strlen",
                testCorrectURL(cppReferenceProcessor, "strlen_s"));
    }

    @Test
    public void testCppErrorlenS() {
        assertEquals("http://www.cplusplus.com/reference/cstring/strerror/",
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
        assertEquals("http://en.cppreference.com/w/c/io/fscanf",
                testCorrectURL(cppReferenceProcessor, "std::scanf"));
    }

    @Test
    public void testCppScanf() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/scanf/",
                testCorrectURL(cppProcessor, "std::scanf"));
    }

    @Test
    public void testCppRefFWrite() {
        assertEquals("http://en.cppreference.com/w/c/io/fwrite",
                testCorrectURL(cppReferenceProcessor, "std::fwrite"));
    }

    @Test
    public void testCppFWrite() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/fwrite/",
                testCorrectURL(cppProcessor, "std::fwrite"));
    }

    @Test
    public void testCppRefFgetC() {
        assertEquals("http://en.cppreference.com/w/c/io/fgetc",
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
    public void testCppGetWC() {
        assertEquals("http://www.cplusplus.com/reference/cwchar/getwc/",
                testCorrectURL(cppProcessor, "std::getwc"));
    }

    @Test
    public void testCppFgetWC() {
        assertEquals("http://www.cplusplus.com/reference/cwchar/fgetwc/",
                testCorrectURL(cppProcessor, "std::fgetwc"));
    }

    @Test
    public void testCppRefWScanf() {
        assertEquals("http://en.cppreference.com/w/c/io/fwscanf",
                testCorrectURL(cppReferenceProcessor, "std::wscanf"));
    }

    @Test
    public void testCppWScanf() {
        assertEquals("http://www.cplusplus.com/reference/cwchar/wscanf/",
                testCorrectURL(cppProcessor, "std::wscanf"));
    }

    @Test
    public void testCppRefFScanfS() {
        assertEquals("http://en.cppreference.com/w/c/io/fscanf",
                testCorrectURL(cppReferenceProcessor, "std::fscanf_s"));
    }

    @Test
    public void testCppFScanfS() {
        assertEquals("http://www.cplusplus.com/reference/cstdio/fscanf/",
                testCorrectURL(cppProcessor, "std::fscanf_s"));
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

    @Test
    public void testCppRefIsWAlpha() {
        assertEquals("http://en.cppreference.com/w/c/string/wide/iswalpha",
                testCorrectURL(cppReferenceProcessor, "std::iswalpha"));
    }

    @Test
    public void testCppIsWAlpha() {
        assertEquals("http://www.cplusplus.com/reference/cwctype/iswalpha/",
                testCorrectURL(cppProcessor, "std::iswalpha"));
    }

    @Test
    public void testCppRefWscLenS() {
        assertEquals("http://en.cppreference.com/w/c/string/wide/wcslen",
                testCorrectURL(cppReferenceProcessor, "std::wcslen_s"));
    }

    @Test
    public void testCppIsWscLenS() {
        assertEquals("http://www.cplusplus.com/reference/cwchar/wcslen/",
                testCorrectURL(cppProcessor, "std::wcslen_s"));
    }

    public String testCorrectURL(SiteProcessor processor, String query) {
        return processor.generateRequestURL(query);
    }
}
