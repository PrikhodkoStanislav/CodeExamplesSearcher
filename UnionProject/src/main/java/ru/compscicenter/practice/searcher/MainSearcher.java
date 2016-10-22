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
            SelfProjectSearcher searcher2;
            List<CodeExample> l1;
            List<CodeExample> l2;

            CommandLine cmd = commandLine.parseArguments(args);

            if (cmd.hasOption("online") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") ||
                    cmd.hasOption("all") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") && cmd.hasOption("offline"))
                throw new ParseException("You must enter only one option!");

            String functionName = args[1];
            if (args[args.length - 1].matches("html|txt")) {
                format = args[args.length - 1];
            }
            check(format);

            String path = "";
            if (cmd.hasOption("online")) {
                String[] vals = cmd.getOptionValues("online");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                searcher1 = new SiteSearcher();
                l1 = searcher1.search(functionName);
                processResults(l1);
            } else if (cmd.hasOption("offline")) {
                String[] vals = cmd.getOptionValues("offline");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                if (vals.length > 1) {
                    path = vals[1];
                }

                searcher2 = new SelfProjectSearcher(path);
                l2 = searcher2.search(functionName);
                processResults(l2);
            } else if (cmd.hasOption("all")) {
                String[] vals = cmd.getOptionValues("all");
                if (vals == null)
                    throw new ParseException("Option has required arguments!");

                if (vals.length > 1) {
                    path = vals[1];
                }

                searcher1 = new SiteSearcher();
                searcher2 = new SelfProjectSearcher(path);
                l1 = searcher1.search(functionName);
                l2 = searcher2.search(functionName);
                l1.addAll(l2);
                processResults(l1);
            } else if (cmd.hasOption("help")) {
                if (cmd.getOptions().length <= 1)
                    commandLine.printHelp();
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            commandLine.printHelp();
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
            String file = codeFormatter.createResultFile(l1, format);

            String path = "";
            if (!format.isEmpty()) {
                if (format.matches("html")) {
                    path = "examples.html";
                } else if (format.matches("txt")) {
                    path = "examples.txt";
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                    writer.write(file);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(file);
            }

        } else {
            System.out.println("No such example found!");
        }
    }
}
