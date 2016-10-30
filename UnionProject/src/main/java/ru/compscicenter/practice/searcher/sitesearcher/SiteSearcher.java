package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.codeexample.SiteCodeExample;

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
            noExample.add(
                    new SiteCodeExample("C", "CPLUSPLUS.RU/CPPREFERENCE.COM", "No such method found!"));
            return noExample;
        }

        return answers;
    }
}
