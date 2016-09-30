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
            String request = processor.generateRequestURL(queryMethod);
            if (request == null || "".equals(request)) {
                System.out.println("Please, exact yor function name");
            } else {
                try {
                    String webContent = processor.sendGet(request);
                    if (webContent.contains("Page Not Found")) {
                        System.out.println("No such method found!");
                        return;
                    } else {
                        List<String> answers = processor.findAndProcessCodeExamples(webContent);
                        for (String answer : answers) {
                            System.out.println(answer);
                            System.out.println();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
