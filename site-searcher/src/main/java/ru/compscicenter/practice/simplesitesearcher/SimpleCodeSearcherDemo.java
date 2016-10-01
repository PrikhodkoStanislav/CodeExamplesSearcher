package ru.compscicenter.practice.simplesitesearcher;

import java.util.List;
import java.util.Scanner;

/**
 * Created by user on 24.09.2016!
 */
public class SimpleCodeSearcherDemo {
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        SiteProcessor[] processors = {new CPlusPlusSiteProcessor(), new CPPReferenceSiteProcessor()};

        System.out.print("Enter a function name: ");
        String queryMethod = in.nextLine();

        for (SiteProcessor processor : processors) {
            processor.setQuery(queryMethod);
            processor.start();
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
