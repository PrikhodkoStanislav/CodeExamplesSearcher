package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.*;

/**
 * Created by user on 15.10.2016!
 */
public class CommandLineSearcher {
    private static CommandLineSearcher instanceOf;
    private final Options options;

    private CommandLineSearcher() {
        Option help = Option.builder("help")
                .longOpt("help")
                .desc("All functions of this utility")
                .build();

        Option online = Option.builder("w")
                .longOpt("online")
                .optionalArg(true)
                .desc("Search code examples only on web sites")
                .build();

        Option offline = Option.builder("s")
                .longOpt("offline")
                .optionalArg(true)
                .desc("Search code examples only in the a project")
                .build();

        Option all = Option.builder("a")
                .longOpt("all")
                .optionalArg(true)
                .desc("Search code examples both on web sites and in a project")
                .build();

        Option function = Option.builder("f")
                .longOpt("func")
                .numberOfArgs(1)
                .argName("func_name")
                .required()
                .desc("Function name, required")
                .build();

        Option path = Option.builder("p")
                .longOpt("path")
                .numberOfArgs(1)
                .argName("path_name")
                .desc("Path to project")
                .build();

        options = new Options();
        options.addOption(online)
                .addOption(offline)
                .addOption(all)
                .addOption(function)
                .addOption(help)
        .       addOption(path);
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
        formatter.printHelp("search [-options] [<result_format>]",
                "Code example searcher", options,
                "Result format can be:\n    html        return HTML-file\n    txt         return TXT-file\n" +
                        "    no type     print results on the screen");
    }
}
