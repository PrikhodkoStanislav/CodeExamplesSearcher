package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
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
    private static String format = "";

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("There are no command for program!");
            System.exit(0);
        }

        try {
            Searcher searcher1;
            List<CodeExample> l1;

            CommandLine cmd = commandLine.parseArguments(args);

            if (cmd.hasOption("online") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") ||
                    cmd.hasOption("all") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") && cmd.hasOption("offline"))
                throw new ParseException("You must enter only one option!");

            String functionName;
            String path;
            if (cmd.hasOption("online")) {
                String[] vals = cmd.getOptionValues("online");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                functionName = vals[0];
                if (vals.length > 1) {
                    format = vals[1];
                }

                check(format);

                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
                processResults(l1);
            } else if (cmd.hasOption("offline")) {
                String[] vals = cmd.getOptionValues("offline");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                functionName = vals[0];
                if (vals.length > 1) {
                    path = vals[1];
                }

                if (vals.length > 2) {
                    format = vals[2];
                }

                check(format);
                //search in SRC
                //l2 = searcher2.search(functionName);
                //processResults(l2, format);
            } else if (cmd.hasOption("all")) {
                String[] vals = cmd.getOptionValues("all");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                functionName = vals[0];
                if (vals.length > 1) {
                    path = vals[1];
                }

                if (vals.length > 2) {
                    format = vals[2];
                }

                check(format);
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
            System.out.println(e.getMessage());
            commandLine.printHelp();
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
        }
    }

    private static void check(String format) throws ParseException {
        if (!format.matches("(html|txt|)"))
            throw new ParseException("This file extension is not supported!");
    }

    private static void processResults(List<CodeExample> l1) throws ParseException {
        //TODO remove duplicates
        if (l1 != null) {
            CodeFormatter codeFormatter = new CodeFormatter();
            codeFormatter.beautifyCode(l1);
            codeFormatter.createResultFile(l1, format);
        } else {
            System.out.println("No such example found!");
        }
    }
}
