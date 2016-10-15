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
        options.addOption("on", "online", false, "search code examples on web sites");
        options.addOption("off", "offline", false, "search code examples in project");
        options.addOption("a", "all", false, "search code examples on web sites and in project");
        options.addOption("f", "file", true, "function name");
        options.addOption("p", "path", true, "name of the project root");
        options.addOption("h", "help", false, "All functions of this utility");
    }

    public CommandLine parseArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public void printHelp() {
        System.out.println(options.getOption("help").getDescription());
        for (Option option : options.getOptions()) {
            System.out.println(" -" + option.getOpt() + ", " +
                    " --" + (option.getLongOpt() != null ? option.getLongOpt() : "") +
                    (option.hasArg() ? "[[=\\s]<" + option.getLongOpt() + "_name>]" : "") +
                     " --- " + option.getDescription());
        }
    }
}
