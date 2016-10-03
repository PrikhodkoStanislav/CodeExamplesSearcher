package ru.compscicenter.practice.simplesitesearcher;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void test() {
        String query = "sin";
        Assert.assertEquals("http://en.cppreference.com/w/cpp/numeric/math/sin",
                testCorrectURL(cppReferenceProcessor, query));
    }

    public String testCorrectURL(SiteProcessor processor, String query) {
        return processor.generateRequestURL(query);
    }
}
