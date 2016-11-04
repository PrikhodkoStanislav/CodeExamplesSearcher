package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.algorithms.AlgorithmsRemoveDuplicates;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Станислав on 05.10.2016!
 */
public class MainSearcher {
    private final static Logger logger = Logger.getLogger(MainSearcher.class);

    private static CommandLineSearcher commandLine = CommandLineSearcher.getInstanceOf();
    private static String format = "";

    public static void main(String[] args) {
        logger.setLevel(Level.ERROR);

        Searcher[] searchers;
        List<CodeExample> l1;

        if (args.length <= 0) {
            System.out.println("There are no command for program!");
            System.exit(0);
        } else if (args.length == 1 && args[0].matches("--?help")) {
            commandLine.printHelp();
            System.exit(0);

        }

        try {
            CommandLine cmd = commandLine.parseArguments(args);

            if (cmd.hasOption("online") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") ||
                    cmd.hasOption("all") && cmd.hasOption("offline") ||
                    cmd.hasOption("all") && cmd.hasOption("online") && cmd.hasOption("offline"))
                throw new ParseException("You must enter only one option!");

            String functionName = args[1];
            String path = "";
            if (args.length > 2) {
                if (args[args.length - 1].matches("html|txt")) {
                    format = args[args.length - 1].toLowerCase();
                    if (args[args.length - 2].startsWith("C:\\") || args[args.length - 2].startsWith("D:\\"))
                        path = args[args.length - 2];
                } else if (args[args.length - 1].startsWith("C:\\") || args[args.length - 1].startsWith("D:\\")) {
                    path = args[args.length - 1];
                }
                check(format);
            } else if (args.length <= 1) {
                throw new ParseException("Option has required arguments!");
            }

            searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(path)};
            l1 = new ArrayList<>();

            if (cmd.hasOption("help")) {
                if (cmd.getOptions().length <= 1)
                    commandLine.printHelp();
            } else {
                if (hasSearchOptions(cmd)) {
                    System.out.println("Start searching ...");
                    if (cmd.hasOption("online")) {
                        l1.addAll(searchers[0].search(functionName));
                    } else if (cmd.hasOption("offline")) {
                        l1.addAll(searchers[1].search(functionName));
                    } else if (cmd.hasOption("all")) {
                        for (Searcher searcher : searchers) {
                            l1.addAll(searcher.search(functionName));
                        }
                    }
                    System.out.println("End searching ...");
                    processResults(l1);
                } else {
                    throw new ParseException("Enter one of three search options: -a, -s or -w");
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            commandLine.printHelp();
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
    }

    private static boolean hasSearchOptions(CommandLine cmd) {
        return cmd.hasOption("online") ||
                cmd.hasOption("offline") ||
                cmd.hasOption("all");
    }

    private static void check(String format) throws ParseException {
        if (!format.matches("(html|txt|)"))
            throw new ParseException("This file extension is not supported!");
    }

    private static void processResults(List<CodeExample> l1) throws ParseException, IOException {
        if (l1 != null) {
            ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
            projectCodeFormatter.beautifyCode(l1);

            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.EqualsTokens;
            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(l1, typeOfCompareResult);
            l1 = duplicateRemover.removeDuplicates();

            String fileText = projectCodeFormatter.createResultFile(l1, format);

            String path = "result" + File.separator + "examples";
            if (!format.isEmpty()) {
                if (format.matches("html")) {
                    path += ".html";
                } else if (format.matches("txt")) {
                    path += ".txt";
                }

                File file = new File(path);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(fileText);
                    writer.close();
                } catch (Exception e) {
                    logger.error("Sorry, something wrong!", e);
                    e.printStackTrace();
                }
                System.out.println("Find data in folder 'result'");
            } else {
                System.out.println(fileText);
            }

        } else {
            System.out.println("No such example found!");
        }
    }
}
