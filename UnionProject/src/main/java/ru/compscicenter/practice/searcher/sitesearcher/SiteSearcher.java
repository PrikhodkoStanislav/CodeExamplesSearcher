package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.codeexample.SiteCodeExample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher implements Searcher {

    @Override
    public List<CodeExample> search(String methodNameQuery) {
        SiteProcessor[] processors = {new CPlusPlusSiteProcessor(), new CPPReferenceSiteProcessor()};

        for (SiteProcessor processor : processors) {
            processor.setQuery(methodNameQuery);
            processor.start();
        }

        for (SiteProcessor processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
