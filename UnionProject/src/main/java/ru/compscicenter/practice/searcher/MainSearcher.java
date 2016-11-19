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
    private static List<CodeExample> codeFromSublime = null;

    private static boolean searchOnSites = true;
    private static boolean searchInProject = true;

    /**
     * Search code examples for Sublime server
     * @param funcName name of function
     * @param pathForSearch path to the directory with files for search
     * @param pathFromSublime path to the open file in sublime
     * @param line number line where cursor in sublime
     * @return html-string with code examples
     * */
    public static String searchExamplesForClient(String funcName, String pathForSearch,
                                                 String pathFromSublime, int line)
            throws IOException, ParseException {
        format = "html";
        functionName = funcName;
        Searcher[] searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(pathForSearch),
                new SelfProjectSearcher(pathFromSublime)};
        List<CodeExample> l1 = new ArrayList<>();

        List<CodeExample> dbExamples = DATABASE.loadByLanguageAndFunction("C", funcName);
        if (dbExamples == null || dbExamples.size() == 0) {
            l1.addAll(searchers[0].search(funcName));
            l1.stream().filter(codeExample -> codeExample.getSource().contains("cplusplus") ||
                    codeExample.getSource().contains("cppreference")).forEach(DATABASE::save);
        } else {
            updateDB(l1);
            l1 = dbExamples;
        }

        l1.addAll(searchers[1].search(funcName));
        codeFromSublime = new ArrayList<>();
        List<CodeExample> cesFromActiveProject = searchers[2].search(funcName);
        for (CodeExample ce : cesFromActiveProject) {
            if (ce.getLineWithFunction() == line) {
                codeFromSublime.add(ce);
                break;
            }
        }
        return htmlWithResult(l1);
    }

    /**
     * Parse cmd arguments and assign values to program fields
     * @param args input arguments
     * */
    private static void parseCmdArguments(String[] args) throws ParseException {
        if (!args[0].startsWith("-")) {
            throw new ParseException("First parameter must be an option!");
        }

        CommandLine cmd = commandLine.parseArguments(args);

        if (cmd.hasOption("online") && cmd.hasOption("offline") ||
                cmd.hasOption("all") && cmd.hasOption("online") ||
                cmd.hasOption("all") && cmd.hasOption("offline") ||
                cmd.hasOption("all") && cmd.hasOption("online") && cmd.hasOption("offline")) {
            throw new ParseException("You must enter only one option!");
        }

        if (hasSearchOptions(cmd)) {
            if (cmd.hasOption("online")) {
                searchInProject = false;
            } else if (cmd.hasOption("offline")) {
                searchOnSites = false;
            }
        } else {
            throw new ParseException("Enter one of three search options: -a, -s or -w");
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

        if (!arg.matches("--?(help|server)") && !arg.matches("-j")) {
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
        ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
        if (codeFromSublime != null) {
            projectCodeFormatter.beautifyCode(codeFromSublime);
        }
        if (examples != null) {
            projectCodeFormatter.beautifyCode(examples);

            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.LevenshteinDistance;
            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(examples, typeOfCompareResult);
            examples = duplicateRemover.removeDuplicates();
            result = projectCodeFormatter.createResultFile(functionName, examples, format, codeFromSublime);
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

            String fileText = projectCodeFormatter.createResultFile(functionName, examples, format, null);

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
                } else if (codeExample.getModificationDate() <
                        example.getModificationDate()) {
                    example.setCodeExample(codeExample.getCodeExample());
                    DATABASE.save(example);
                }
            } else {
                DATABASE.save(codeExample);
            }
        }
    }

    public static void main(String[] args) {

        //TODO set timer
        //Timer timer = new Timer();
        // timer.schedule(new UpdateDBTask(), 24 * 3600 * 1000);

        logger.setLevel(Level.ERROR);

        try {
            // check that arguments exists and print help
            if (args.length <= 0) {
                System.out.println("There are no command for program!");
                System.exit(0);
            } else if (args.length == 1){
                parseSingleArgument(args[0]);
            } else {
                //parse cmd arguments
                parseCmdArguments(args);

                // prepare searchers and list for results
                Searcher[] searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(path)};
                List<CodeExample> results = new ArrayList<>();

                System.out.println("Start searching ...");
                if (searchOnSites) {
                    List<CodeExample> dbExamples = DATABASE.loadByLanguageAndFunction("C", functionName);
                    if (dbExamples == null || dbExamples.size() == 0) {
                        results.addAll(searchers[0].search(functionName));
                        results.stream().filter(codeExample -> codeExample.getSource().contains("cplusplus") ||
                                codeExample.getSource().contains("cppreference")).forEach(DATABASE::save);
                    } else {
                        updateDB(results);
                        results = dbExamples;
                    }
                }
                if (searchInProject) {
                    results.addAll(searchers[1].search(functionName));
                }

                System.out.println("End searching ...");
                processResults(results);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            commandLine.printHelp();
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
    }
}
