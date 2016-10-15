package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.*;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

/**
 * Created by user on 15.10.2016!
 */
public class CommandLineSearcher {
    private Options options = new Options();

    public CommandLineSearcher() {
        //https://commons.apache.org/proper/commons-cli/usage.html
        //https://habrahabr.ru/post/123360/
        options.addOption(new Option("online", "search code examples on web sites"));
        options.addOption(new Option("offline", "search code examples in project"));
        options.addOption(new Option("all", "search code examples on web sites and in project"));
        options.addOption("f", true, "function name");
        options.addOption("p", true, "name of the project root");
        options.addOption("help", false, "help for utility");
    }

    public CommandLine parseArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
