package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.sitesearcher.CodeFormatter;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Станислав on 05.10.2016.
 */
public class MainSearcher {
    private static CommandLineSearcher commandLine = new CommandLineSearcher();
    private static CodeFormatter codeFormatter = CodeFormatter.getInstance();
    private static String functionName;
    private static String path;

    public static void main(String[] args) {
        Searcher searcher1;
        List<CodeExample> l1 = null;

        try {
            CommandLine cmd = commandLine.parseArguments(args);
            if (cmd.hasOption("f")) {
                functionName = cmd.getOptionValue("f");
            }
            if (cmd.hasOption("p")) {
                path = cmd.getOptionValue("p");
            }

            if (cmd.hasOption("online")) {
                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
            } else if (cmd.hasOption("offline")) {
                //search in SRC
                //l2 = searcher2.search(functionName);
            } else {
                //search in SRC and on sites
                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
                //l2 = searcher2.search(functionName);
            }
        } catch (ParseException e) {
            System.out.println("Wrong arguments");
        }

        String s2 = "";
        SelfProjectSearcher searcher2 = new SelfProjectSearcher();

        if (args.length > 1) {
            s2 = searcher2.search(args[0], args[1]);
            //TODO method return List<CodeExample>
            //l1.addAll(l2);
//            s2 = searcher2.search("strlen", "../UnionProject/src/main/resources");
//            for (CodeExample s  : searcher2.list) {
//                System.out.println(s.toString());
//            }
//            CodeDuplicateRemover.run(searcher2.list);
//            System.out.println(s2);
        }

        //TODO remove duplicates
        if (l1 != null) {
            for (CodeExample codeExample : l1) {
                codeExample.setCodeExample(codeFormatter.toPrettyCode(codeExample.codeExample));
            }

            codeFormatter.createHTML(l1);
        } else {
            System.out.println("No such example found!");
        }

        if (args.length > 1) {
            String path2 = "examplesFromProject.html";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path2))) {
                writer.write(s2);
                writer.newLine();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File htmlFile2 = new File(path2);
            try {
                Desktop.getDesktop().browse(htmlFile2.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
