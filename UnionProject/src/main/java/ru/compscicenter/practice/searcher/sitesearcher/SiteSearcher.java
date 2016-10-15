package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher extends Searcher {

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
        for (SiteProcessor processor : processors) {
            answers.addAll(processor.getAnswers());
        }

        return answers;
    }
}
