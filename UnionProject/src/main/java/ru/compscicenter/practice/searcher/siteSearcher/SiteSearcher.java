package ru.compscicenter.practice.searcher.siteSearcher;

import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher {
    public void search(String queryMethod) {
        SiteProcessor[] processors = {new CPlusPlusSiteProcessor(), new CPPReferenceSiteProcessor()};

        System.out.print("Enter a function name: ");

        for (SiteProcessor processor : processors) {
            processor.setQuery(queryMethod);
            processor.start();
        }

        for (SiteProcessor processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Examples of this method usage:");
        for (SiteProcessor processor : processors) {
            System.out.println(processor.getSiteName());
            List<String> answers = processor.getAnswers();
            answers.forEach(System.out::println);
        }
    }
}
