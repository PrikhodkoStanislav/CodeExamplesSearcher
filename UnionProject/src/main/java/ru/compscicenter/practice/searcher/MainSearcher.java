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
    private static CommandLineSearcher commandLine = CommandLineSearcher.getInstanceOf();
    private static CodeFormatter codeFormatter = CodeFormatter.getInstance();
    private static String functionName;
    private static String path;

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("There are no command for program!");
            System.exit(0);
        }

        try {
            Searcher searcher1;
            List<CodeExample> l1 = null;

            CommandLine cmd = commandLine.parseArguments(args);

            if (cmd.hasOption("online")) {
                functionName = cmd.getOptionValues("online")[0];
                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
                processResults(l1);
            } else if (cmd.hasOption("offline")) {
                String[] vals = cmd.getOptionValues("offline");
                functionName = vals[0];
                if (vals.length > 1) {
                    path = vals[1];
                }
                //search in SRC
                //l2 = searcher2.search(functionName);
            } else if (cmd.hasOption("all")) {
                String[] vals = cmd.getOptionValues("all");
                functionName = vals[0];
                if (vals.length > 1) {
                    path = vals[1];
                }
                //search in SRC and on sites
                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
                //l2 = searcher2.search(functionName);
                processResults(l1);
            } else if (cmd.hasOption("help")) {
                if (cmd.getOptions().length <= 1)
                    commandLine.printHelp();
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

    private static void processResults(List<CodeExample> l1) {
        //TODO remove duplicates
        if (l1 != null) {
            for (CodeExample codeExample : l1) {
                codeExample.setCodeExample(codeFormatter.toPrettyCode(codeExample.codeExample));
            }

            codeFormatter.createHTML(l1);
        } else {
            System.out.println("No such example found!");
        }
    }
}
