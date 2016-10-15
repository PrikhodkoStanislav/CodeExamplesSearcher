package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Created by user on 15.10.2016!
 */
public class CommandLineSearcher {
    private Options options = new Options();

    public CommandLineSearcher() {
        options.addOption(new Option("online", "search code examples on web sites"));
        options.addOption(new Option("offline", "search code examples in project"));
        options.addOption("func", true, "function name");
        options.addOption("path", true, "name of the project root");
        options.addOption("help", false, "help for utility");
    }
}
