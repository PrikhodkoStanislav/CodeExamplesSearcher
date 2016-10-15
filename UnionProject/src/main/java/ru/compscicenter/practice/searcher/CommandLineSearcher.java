package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.*;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

/**
 * Created by user on 15.10.2016!
 */
public class CommandLineSearcher {
    private static CommandLineSearcher instanceOf;
    private final Options options;

    //https://commons.apache.org/proper/commons-cli/usage.html
    //https://habrahabr.ru/post/123360/
    private CommandLineSearcher() {
        options = new Options();
        options.addOption("on", "online", false, "search code examples on web sites")
                .addOption("off", "offline", false, "search code examples in project")
                .addOption("a", "all", false, "search code examples on web sites and in project")
                .addOption("f", "file", true, "function name")
                .addOption("p", "path", true, "name of the project root")
                .addOption(null, "help", false, "All functions of this utility");
    }

    public static CommandLineSearcher getInstanceOf() {
        if (instanceOf == null)
            instanceOf = new CommandLineSearcher();
        return instanceOf;
    }

    public CommandLine parseArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }
}
