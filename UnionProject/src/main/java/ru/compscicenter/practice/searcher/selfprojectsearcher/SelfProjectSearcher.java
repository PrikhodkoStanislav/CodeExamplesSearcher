package ru.compscicenter.practice.searcher.selfprojectsearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Станислав on 05.10.2016!
 */
public class SelfProjectSearcher implements Searcher {
    private final static Logger logger = Logger.getLogger(SelfProjectSearcher.class);

    private List<CodeExample> list = new ArrayList<>();

    private String startPath;

    public SelfProjectSearcher(String startPath) {
        this.startPath = startPath;
    }

    private void searchInFile(String functionName, String pathToFile) {
        logger.setLevel(Level.INFO);

        final String newLine = "\n";

        Pattern patternForFunctionName = Pattern.compile(".*(\\s)(" + functionName + ")(\\().+");
        Pattern patternForOpenBracket = Pattern.compile(".*\\{.*");
        Pattern patternForCloseBracket = Pattern.compile(".*\\}.*");
        Pattern patternForOpenCloseBracket = Pattern.compile(".*\\{.*\\}.*");
        Pattern patternForCloseOpenBracket = Pattern.compile(".*\\}.*\\{.*");

        try {
            FileReader fileReader = new FileReader(pathToFile);
            BufferedReader in = new BufferedReader(fileReader);
            String str;
            int strNumber = 0;
            List<String> buffer = new LinkedList<>();

            while ((str = in.readLine()) != null) {
                strNumber++;
                Matcher matcherForFunctionName = patternForFunctionName.matcher(str);

                if (matcherForFunctionName.matches()) {
                    StringBuilder sb = new StringBuilder();
                    for(String s : buffer) {
                        sb.append(s);
                        sb.append(newLine);
                    }

                    buffer.clear();

                    sb.append(str);
                    sb.append(newLine);
                    str = in.readLine();

                    while ((str != null)) {
                        Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);
                        if (matcherForOpenCloseBracket.matches()) {
                            sb.append(str);
                            sb.append(newLine);
                            str = in.readLine();
                            continue;
                        }
                        Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);
                        if (matcherForCloseBracket.matches()) {
                            sb.append(str);
                            sb.append(newLine);
                            break;
                        }
                        sb.append(str);
                        sb.append(newLine);
                        str = in.readLine();
                    }

                    sb.append(newLine);
                    CodeExample codeExample = new CodeExample();
                    codeExample.setLanguage("C");
                    codeExample.setSource(pathToFile + " : " + strNumber);
                    codeExample.setFunction(functionName);
                    codeExample.setCodeExample(sb.toString());
                    logger.info("Code example parameters: " +
                            "programming lang=" + codeExample.getLanguage() + " " +
                            ", function=" + codeExample.getFunction() + " " +
                            ", source=" + codeExample.getSource());
                    list.add(codeExample);
                    continue;
                }

                Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);

                if (matcherForOpenCloseBracket.matches()) {
                    buffer.add(str);
                    continue;
                }

                Matcher matcherForCloseOpenBracket = patternForCloseOpenBracket.matcher(str);

                if (matcherForCloseOpenBracket.matches()) {
                    buffer.clear();
                    buffer.add(str);
                    continue;
                }

                Matcher matcherForOpenBracket = patternForOpenBracket.matcher(str);

                if (matcherForOpenBracket.matches()) {
                    buffer.clear();
                    buffer.add(str);
                    continue;
                }

                Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);

                if (matcherForCloseBracket.matches()) {
                    buffer.clear();
                    continue;
                }

                buffer.add(str);
            }
        }
        catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
    }

    private void searchInDirectory(String functionName, String pathToFile) {
        File file = new File(pathToFile);
        if (!file.exists()) {
            System.out.println("Wrong path to the file!");
        }
        if (file.isDirectory()) {
            File[] filesInDirectory = file.listFiles();
            if (filesInDirectory == null) {
                return;
            }
            for (File f : filesInDirectory) {
                searchInDirectory(functionName, f.getPath());
            }
        } else {
            searchInFile(functionName, pathToFile);
        }
    }

    public List<CodeExample> search(String functionName) {
        searchInDirectory(functionName, startPath);
        return list;
    }
}
