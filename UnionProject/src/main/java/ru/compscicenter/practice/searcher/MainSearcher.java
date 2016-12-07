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
import ru.compscicenter.practice.searcher.server.UpdateDBTask;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

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
    private static String stringFromRequest = "";

    private static boolean searchOnSites = true;
    private static boolean searchInProject = true;

    private static Preferences prefs = Preferences.userRoot().node("settings");

    private final static int defaultMaxExamplesNumber = 20;

    private final static boolean defaultRestoreDB = false;

    private final static boolean defaultCpp = true;
    private final static boolean defaultCppref = true;
    private final static boolean defaultSearchCode = true;
    private final static boolean defaultStackOverflow = true;

    private final static boolean defaultIncludeDB = true;

    private final static int defaultDuplicator = 1;

    /**
     * Search code examples for Sublime server
     * @param funcName name of function
     * @param pathForSearch path to the directory with files for search
     * @param pathFromSublime path to the open file in sublime
     * @param line number line where cursor in sublime
     * @return html-string with code examples
     * */
    public static String searchExamplesForClient(String funcName, String pathForSearch,
                                                 String pathFromSublime, int line, String string)
            throws IOException, ParseException {
        format = "html";
        functionName = funcName;
        stringFromRequest = string;

        Searcher[] searchers = new Searcher[]{new SiteSearcher(), new SelfProjectSearcher(pathForSearch),
                new SelfProjectSearcher(pathFromSublime)};
        List<CodeExample> l1 = new ArrayList<>();
        List<CodeExample> l2 = new ArrayList<>();

        l1 = tryToCodeExamplesFromDB(searchers[0], l1);

        l2 = searchers[1].search(funcName);

        codeFromSublime = new ArrayList<>();
        List<CodeExample> cesFromActiveProject = searchers[2].search(funcName);
        for (CodeExample ce : cesFromActiveProject) {
            if (ce.getLineWithFunction() == line) {
                codeFromSublime.add(ce);
                break;
            }
        }
        l2 = htmlWithResult(l2);
        l1.addAll(l2);

        ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
        String result = projectCodeFormatter.createResultFile(functionName, l1, format, codeFromSublime,
                stringFromRequest);
        return result;
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
    private static List<CodeExample> htmlWithResult(List<CodeExample> examples) throws ParseException, IOException {
        ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
        if (codeFromSublime != null) {
            projectCodeFormatter.beautifyCode(codeFromSublime);
        }
        if (examples != null) {
            projectCodeFormatter.beautifyCode(examples);

            int duplicator = prefs.getInt("duplicator", defaultDuplicator);
            if (duplicator == 1) {
                AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.LevenshteinDistance;
                CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(examples, typeOfCompareResult);
                examples = duplicateRemover.removeDuplicates();
            } else if (duplicator == 2) {
                AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.EqualsTokens;
                CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(examples, typeOfCompareResult);
                examples = duplicateRemover.removeDuplicates();
            }

            int maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
            int size = examples.size();
            if (size > maxExamplesNumber) {
                examples.removeAll(examples.subList(maxExamplesNumber, size));
            }
        }
        return examples;
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

            String fileText = projectCodeFormatter.createResultFile(functionName, examples, format, null, "");

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

    //if no result at site (stackoverflow, cppreference) -> that search results at site

    private static List<CodeExample> tryToCodeExamplesFromDB(Searcher searcher, List<CodeExample> results) {
        boolean cpp = prefs.getBoolean("cpp", defaultCpp);
        boolean cppref = prefs.getBoolean("cppref", defaultCppref);
        boolean searchCode = prefs.getBoolean("searchCode", defaultSearchCode);
        boolean stackOverflow = prefs.getBoolean("stackOverflow", defaultStackOverflow);
        boolean includeDB = prefs.getBoolean("includeDB", defaultIncludeDB);

        boolean restoreDB = prefs.getBoolean("restoreDB", defaultRestoreDB);

        if (includeDB) {
            if (restoreDB) {
                DATABASE.restore();
            } else {
                UpdateDBTask task = new UpdateDBTask();
                task.run();
            }
            List<CodeExample> dbExamples = DATABASE.loadByLanguageAndFunction("C", functionName);
            if (dbExamples == null || dbExamples.size() == 0) {
                if (cpp) {
                    searcher.getFilter().put("cplusplus", true);
                }
                if (cppref) {
                    searcher.getFilter().put("cppreference", true);
                }
                if (searchCode) {
                    searcher.getFilter().put("searchcode", true);
                }
                if (stackOverflow) {
                    searcher.getFilter().put("stackoverflow", true);
                }
            } else {
                if (cpp && !existsResultsOfSite(dbExamples, "cplusplus")) {
                    searcher.getFilter().put("cplusplus", true);
                }
                if (cppref && !existsResultsOfSite(dbExamples, "cppreference")) {
                    searcher.getFilter().put("cppreference", true);
                }
                if (searchCode && !existsResultsOfSite(dbExamples, "searchcode")) {
                    searcher.getFilter().put("searchcode", true);
                }
                if (stackOverflow && !existsResultsOfSite(dbExamples, "stackoverflow")) {
                    searcher.getFilter().put("stackoverflow", true);
                }
            }

            if (searcher.getFilter().size() >= 1) {
                results.addAll(findResultsOnSites(searcher));
            }

            for (CodeExample result : results) {
                DATABASE.save(result);
            }
            if (dbExamples != null && dbExamples.size() != 0) {
                results.addAll(dbExamples);
            }
        } else {
            if (cpp) {
                searcher.getFilter().put("cplusplus", true);
            }
            if (cppref) {
                searcher.getFilter().put("cppreference", true);
            }
            if (searchCode) {
                searcher.getFilter().put("searchcode", true);
            }
            if (stackOverflow) {
                searcher.getFilter().put("stackoverflow", true);
            }
        }
        return results;
    }

    private static List<CodeExample> findResultsOnSites(Searcher searcher) {
        List<CodeExample> results = new ArrayList<>();
        results.addAll(searcher.search(functionName));
        results.stream().filter(codeExample -> codeExample.getSource().contains("cplusplus") ||
                codeExample.getSource().contains("cppreference") ||
                codeExample.getSource().contains("searchcode") ||
                codeExample.getSource().contains("stackoverflow")
        ).forEach(DATABASE::save);
        return results;
    }

    /**
     * Check any result from website
     * @param dbExamples code examples from database
     * @param siteName website
     * @return true if database examples contains at least one result from website, otherwise false
     * */
    private static boolean existsResultsOfSite(List<CodeExample> dbExamples, String siteName) {
        for (CodeExample dbExample : dbExamples) {
            if (dbExample.getSource().contains(siteName)) {
                return true;
            }
        }
        return false;
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
                    results = tryToCodeExamplesFromDB(searchers[0], results);
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
