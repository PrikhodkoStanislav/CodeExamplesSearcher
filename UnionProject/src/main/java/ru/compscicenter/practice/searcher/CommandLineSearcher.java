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
                .desc("all functions of this utility")
                .build();

        Option online = Option.builder("w")
                .longOpt("online")
                .numberOfArgs(2)
                .optionalArg(true)
                .argName("function> <[result_type]")
                .desc("search code examples only on web sites")
                .build();

        Option offline = Option.builder("s")
                .longOpt("offline")
                .numberOfArgs(3)
                .optionalArg(true)
                .argName("function> <path> <[result_type]")
                .desc("search code examples only in the a project")
                .build();

        Option all = Option.builder("a")
                .longOpt("all")
                .numberOfArgs(3)
                .optionalArg(true)
                .argName("func> <path> <[result_type]")
                .desc("search code examples both on web sites and in a project")
                .build();

        options = new Options();
        options.addOption(online)
                .addOption(offline)
                .addOption(all)
                .addOption(help);
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
        formatter.printHelp("Code examples searcher", options);
        System.exit(0);
    }
}
