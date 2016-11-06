package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.database.CodeExample;
import ru.compscicenter.practice.searcher.Searcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher implements Searcher {
    private final static Logger logger = Logger.getLogger(SiteSearcher.class);

    @Override
    public List<CodeExample> search(String methodNameQuery) {
        logger.setLevel(Level.ERROR);

        SiteProcessor[] processors = {new CPlusPlusSiteProcessor(), new CPPReferenceSiteProcessor()};

        for (SiteProcessor processor : processors) {
            processor.setQuery(methodNameQuery);
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
        if (count == processors.length) {
            List<CodeExample> noExample = new ArrayList<>();
            CodeExample ce = new CodeExample();
            ce.setLanguage("C");
            ce.setSource("CPLUSPLUS.RU/CPPREFERENCE.COM");
            ce.setCodeExample("No such method found!");
            noExample.add(ce);
            return noExample;
        }

        return answers;
    }
}
