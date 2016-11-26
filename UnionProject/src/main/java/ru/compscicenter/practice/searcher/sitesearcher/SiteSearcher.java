package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.database.CodeExample;
import ru.compscicenter.practice.searcher.Searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher implements Searcher {
    private final static Logger logger = Logger.getLogger(SiteSearcher.class);

    public Map<String, Boolean> processorFilter = new HashMap<>();
    public Map<String, SiteProcessor> siteProcessors = new HashMap<>();

    public SiteSearcher() {
        siteProcessors.put("cplusplus", new CPlusPlusSiteProcessor());
        siteProcessors.put("cppreference", new CPPReferenceSiteProcessor());
        siteProcessors.put("searchcode", new SearchCodeProcessor());
        siteProcessors.put("stackoverflow", new StackOverflowSiteProcessor());
    }

    @Override
    public List<CodeExample> search(String methodNameQuery) {
        logger.setLevel(Level.ERROR);

        List<SiteProcessor> processors = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : processorFilter.entrySet()) {
            if (entry.getValue()) {
                processors.add(addSiteProcessorByName(entry.getKey()));
            }
        }

        for (SiteProcessor processor : processors) {
            processor.setQuery(methodNameQuery);
            processor.setLanguage("C");
            processor.start();
        }

        for (SiteProcessor processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                logger.error("Sorry, something wrong!", e);
            }
        }

        List<CodeExample> answers = new ArrayList<>();
        int count = 0;

        for (SiteProcessor processor : processors) {
            if (processor.getAnswers() == null)
                count++;
            else
                answers.addAll(processor.getAnswers());
        }
        if (count == processors.size()) {
            List<CodeExample> noExample = new ArrayList<>();
            CodeExample ce = new CodeExample();
            ce.setLanguage("C");
            ce.setSource("CPLUSPLUS.RU/CPPREFERENCE.COM/STACKOVERFLOW.COM/SEARCHCODE.COM");
            ce.setCodeExample("No such method found!");
            noExample.add(ce);
            return noExample;
        }

        return answers;
    }

    private SiteProcessor addSiteProcessorByName(String key) {
        return siteProcessors.get(key);
    }

    @Override
    public Map<String, Boolean> getFilter() {
        return processorFilter;
    }
}
