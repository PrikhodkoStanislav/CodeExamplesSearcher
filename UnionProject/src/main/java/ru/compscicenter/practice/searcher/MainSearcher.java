package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.algorithms.AlgorithmsRemoveDuplicates;
import ru.compscicenter.practice.searcher.database.CodeExample;
import ru.compscicenter.practice.searcher.database.CodeExampleDA;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.server.CreateServer;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class of examples adviser
 * @author Станислав on 05.10.2016!
 */
public class MainSearcher {
    private final static Logger logger = Logger.getLogger(MainSearcher.class);
    private final static CodeExampleDA DATABASE = CodeExampleDA.getInstance();

    private static CommandLineSearcher commandLine = CommandLineSearcher.getInstanceOf();

    private static String format = "";
    private static String functionName = "";
    private static String path = "";

    /**
     * Search code examples for Sublime server
     * @param funcName name of function
     * @return html-string with code examples
     * */
    public static String searchExamples(String funcName) throws IOException, ParseException {
        path = "./";
        format = "html";
        functionName = funcName;
        Searcher[] searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(path)};
        List<CodeExample> l1 = new ArrayList<>();
        l1.addAll(searchers[0].search(funcName));
        l1.addAll(searchers[1].search(funcName));
//        List<CodeExample> dbExamples = DATABASE.loadByLanguageAndFunction("C", funcName);
//        if (dbExamples == null || dbExamples.size() == 0) {
//            for (CodeExample codeExample : l1) {
//                DATABASE.save(codeExample);
//            }
//        } else {
//            DATABASE.updateDB(l1);
//            dbExamples = DATABASE.loadByLanguageAndFunction("C", funcName);
//        }
        return htmlWithResult(l1);
    }

    public static void main(String[] args) {
        logger.setLevel(Level.ERROR);

        try {
            // check that arguments exists and print help
            if (args.length <= 0) {
                System.out.println("There are no command for program!");
                System.exit(0);
            } else if (args.length == 1){
                parseSingleArgument(args[0]);
            } else {
                CommandLine cmd = commandLine.parseArguments(args);
                if (cmd.hasOption("online") && cmd.hasOption("offline") ||
                        cmd.hasOption("all") && cmd.hasOption("online") ||
                        cmd.hasOption("all") && cmd.hasOption("offline") ||
                        cmd.hasOption("all") && cmd.hasOption("online") && cmd.hasOption("offline")) {
                    throw new ParseException("You must enter only one option!");
                }

                //parse cmd arguments
                parseCmdArguments(args);

                // prepare searchers and list for results
                Searcher[] searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(path)};
                List<CodeExample> results = new ArrayList<>();

                // check options and load searchers for any option
                if (hasSearchOptions(cmd)) {
                    System.out.println("Start searching ...");
                    //todo search data in DB: if data was not found that find data on sites
                    if (cmd.hasOption("online")) {
                        results.addAll(searchers[0].search(functionName));
                    } else if (cmd.hasOption("offline")) {
                        results.addAll(searchers[1].search(functionName));
                    } else if (cmd.hasOption("all")) {
                        for (Searcher searcher : searchers) {
                            results.addAll(searcher.search(functionName));
                        }
                    }

                    // find results in DB
                    List<CodeExample> dbExamples = DATABASE.loadByLanguageAndFunction("C", functionName);
                    if (dbExamples == null || dbExamples.size() == 0) {
                        for (CodeExample codeExample : results) {
                            DATABASE.save(codeExample);
                        }
                    } else {
                        updateDB(results);
                    }
                    dbExamples = DATABASE.loadByLanguageAndFunction("C", functionName);

                    System.out.println("End searching ...");
                    processResults(dbExamples);
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

    /**
     * Parse cmd arguments and assign values to program fields
     * @param args input arguments
     * */
    private static void parseCmdArguments(String[] args) throws ParseException {
        if (!args[0].startsWith("-")) {
            throw new ParseException("First parameter must be an option!");
        }

        if (args.length > 2) {
            if (args[args.length - 1].matches("html|txt")) {
                format = args[args.length - 1].toLowerCase();
                if (args[args.length - 2].startsWith("./") ||
                        args[args.length - 2].startsWith("/") ||
                        args[args.length - 2].startsWith("C:\\") ||
                        args[args.length - 2].startsWith("D:\\")) {
                    functionName = args[args.length - 3];
                    path = args[args.length - 2];
                } else {
                    functionName = args[args.length - 2];
                }
            } else if (args[args.length - 1].startsWith("./") ||
                    args[args.length - 1].startsWith("/") ||
                    args[args.length - 1].startsWith("C:\\") ||
                    args[args.length - 1].startsWith("D:\\")) {
                functionName = args[args.length - 2];
                path = args[args.length - 1];
            } else {
                functionName = args[args.length - 1];
            }
            check(format);
        } else if (args.length == 2) {
            functionName = args[args.length - 1];
        }
    }

    /**
     * Parse first argument and do action depend on option, otherwise throw ParseException
     * @param arg option
     * */
    private static void parseSingleArgument(String arg) throws ParseException {
        if (!arg.startsWith("-")) {
            throw new ParseException("First parameter must be an option!");
        }

        if (arg.matches("-(a|s|w)") || arg.matches("--(all|online|offline)")) {
            throw new ParseException("Option has required arguments!");
        }

        if (!arg.matches("--?(help|server)")) {
            throw new ParseException("This option is not supported!");
        }

        if (arg.matches("--?help")) {
            commandLine.printHelp();
            System.exit(0);
        } else if (arg.equals("--server") || arg.equals("-j")) {
            CreateServer.startServer();
        }
    }

    /**
     * Checks one of the search options
     * @param cmd command line with user options
     * @return true if user typed one of search options, otherwise false
     * */
    private static boolean hasSearchOptions(CommandLine cmd) {
        return cmd.hasOption("online") ||
                cmd.hasOption("offline") ||
                cmd.hasOption("all");
    }

    /**
     * Checks extension types
     * if user writes correct file type this method won't return anything, otherwise will throw ParseException
     * @param format extension type
     * */
    private static void check(String format) throws ParseException {
        if (!format.matches("(html|txt|)"))
            throw new ParseException("This file extension is not supported!");
    }

    /**
     * Creates html-string with code examples for Sublime server
     * @param examples list with search results
     * @return html-string
     * */
    private static String htmlWithResult(List<CodeExample> examples) throws ParseException, IOException {
        String result = "";
        if (examples != null) {
            ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
            projectCodeFormatter.beautifyCode(examples);

            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.LevenshteinDistance;
            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(examples, typeOfCompareResult);
            examples = duplicateRemover.removeDuplicates();
            result = projectCodeFormatter.createResultFile(functionName, examples, format);
        }
        return result;
    }

    /**
     * Process code examples (format code, remove duplicates, print results to the console or write them to txt/html)
     * @param examples list with search results
     * */
    private static void processResults(List<CodeExample> examples) throws ParseException, IOException {
        if (examples != null) {
            ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
            projectCodeFormatter.beautifyCode(examples);

            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.LevenshteinDistance;
            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(examples, typeOfCompareResult);
            examples = duplicateRemover.removeDuplicates();

            String fileText = projectCodeFormatter.createResultFile(functionName, examples, format);

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

    /**
     * Update database with code examples
     * @param examples list with code examples
     * */
    public static void updateDB(List<CodeExample> examples) {
        for (CodeExample codeExample : examples) {
            List<CodeExample> ce = DATABASE.loadByLanguageFunctionAndSource(
                    codeExample.getLanguage(),
                    codeExample.getFunction(),
                    codeExample.getSource());

            if (ce != null && ce.size() != 0) {
                CodeExample example = ce.get(0);
                if (example == null) {
                    DATABASE.save(codeExample);
                } else if (codeExample.getCodeExample().split(" ").length <
                        example.getCodeExample().split(" ").length) {
                    example.setCodeExample(codeExample.getCodeExample());
                    DATABASE.save(example);
                }
            } else {
                DATABASE.save(codeExample);
            }
        }
    }
}
