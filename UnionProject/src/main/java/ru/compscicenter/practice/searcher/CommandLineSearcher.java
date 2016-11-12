package ru.compscicenter.practice.searcher;

import org.apache.commons.cli.*;

/**
 * Created by user on 15.10.2016!
 */
public class CommandLineSearcher {
    private static CommandLineSearcher instanceOf;
    private final Options options;

    /**
     * Fill command line with option
     * */
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

        Option server = Option.builder("server")
                .longOpt("server")
                .desc("Load Jetty server")
                .build();

        options = new Options();
        options.addOption(online)
                .addOption(offline)
                .addOption(all)
                .addOption(help)
                .addOption(server);
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

    /**
     * Print help to the console
     * */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(150, "search -options <function> [<path>] [<result_format>]",
                "Code example searcher", options,
                "In order to execute searcher needs one of three options: -a, -s or -w\n" +
                        "\nResult format can be:\n" +
                        "    html        return HTML-file\n" +
                        "    txt         return TXT-file\n" +
                        "    no type     print results on the screen");
    }
}
