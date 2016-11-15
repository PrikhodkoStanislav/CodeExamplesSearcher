package ru.compscicenter.practice.searcher.selfprojectsearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Станислав on 05.10.2016!
 */
public class SelfProjectSearcher implements Searcher {
    private final static Logger logger = Logger.getLogger(SelfProjectSearcher.class);

    private List<CodeExample> list = new ArrayList<>();

    private String startPath;

    private final String newLine = "\n";

    private int lengthOfStringNumber = 1;

    public SelfProjectSearcher(String startPath) {
        this.startPath = startPath;
    }

    private int lengthStrNumber(int strNumber) {
        int length = 0;
        int number = strNumber;
        if (number == 0) {
            return 1;
        }
        while (number != 0) {
            number /= 10;
            length++;
        }
        return length;
    }

    private String newLineWithNumber(int strNumber) {
        return newLine + ((Integer) strNumber).toString();
    }

    private String strWithNumber(String str, int strNumber) {
        return ((Integer) strNumber).toString() + str;
    }

    private void searchInFileAllFunction(String functionName, String pathToFile) {
        logger.setLevel(Level.INFO);

        try {
            FileReader fileReader = new FileReader(pathToFile);
            File file = new File(pathToFile);
            BufferedReader in = new BufferedReader(fileReader);
            String str = "";
            int strNumber = 0;
            List<String> buffer = new ArrayList<>();

            Stack<Character> stack = new Stack<>();

            while ((str = in.readLine()) != null) {
                strNumber++;
                char[] chars = str.toCharArray();
                for (char c : chars) {
                    if (c == '{') {
                        stack.add(c);
                    } else if (c == '}') {
                        stack.pop();
                    }
                }

                if (str.contains(" " + functionName + "(")) {
                    StringBuilder sb = new StringBuilder();
                    for(String s : buffer) {
                        sb.append(s);
                        sb.append(newLine);
                    }

                    buffer.clear();

                    sb.append(str);
                    sb.append(newLine);
                    while ((str = in.readLine()) != null) {
                        strNumber++;
                        if (stack.empty()) {
                            break;
                        }
                        sb.append(str);
                        sb.append(newLine);
                    }
                    sb.append(newLine);

                    CodeExample codeExample = new CodeExample();
                    codeExample.setLanguage("C");
                    codeExample.setSource(pathToFile + " : " + strNumber);
                    codeExample.setFunction(functionName);
                    codeExample.setCodeExample(sb.toString());
                    codeExample.setModificationDate(file.lastModified());
                    logger.info("Code example parameters: " +
                            "programming lang=" + codeExample.getLanguage() + " " +
                            ", function=" + codeExample.getFunction() + " " +
                            ", source=" + codeExample.getSource() + " " +
                            ", modificationDate" + codeExample.getModificationDate());
                    list.add(codeExample);

                } else {
                    buffer.add(str);
                }

//                Matcher matcherForFunctionName = patternForFunctionName.matcher(str);

//                if (matcherForFunctionName.matches()) {
//                    StringBuilder sb = new StringBuilder();
//                    for(String s : buffer) {
//                        sb.append(s);
//                        sb.append(newLine);
//                    }
//
//                    buffer.clear();
//
//                    sb.append(str);
//                    sb.append(newLine);
//                    str = in.readLine();
//
//                    while ((str != null)) {
//                        Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);
//                        if (matcherForOpenCloseBracket.matches()) {
//                            sb.append(str);
//                            sb.append(newLine);
//                            str = in.readLine();
//                            continue;
//                        }
//                        Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);
//                        if (matcherForCloseBracket.matches()) {
//                            sb.append(str);
//                            sb.append(newLine);
//                            break;
//                        }
//                        sb.append(str);
//                        sb.append(newLine);
//                        str = in.readLine();
//                    }
//
//                    sb.append(newLine);
//                    CodeExample codeExample = new CodeExample();
//                    codeExample.setLanguage("C");
//                    codeExample.setSource(pathToFile + " : " + strNumber);
//                    codeExample.setFunction(functionName);
//                    codeExample.setCodeExample(sb.toString());
//                    codeExample.setModificationDate(file.lastModified());
//                    logger.info("Code example parameters: " +
//                            "programming lang=" + codeExample.getLanguage() + " " +
//                            ", function=" + codeExample.getFunction() + " " +
//                            ", source=" + codeExample.getSource() + " " +
//                            ", modificationDate" + codeExample.getModificationDate());
//                    list.add(codeExample);
//                    continue;
//                }
//
//                Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);
//
//                if (matcherForOpenCloseBracket.matches()) {
//                    buffer.add(str);
//                    continue;
//                }
//
//                Matcher matcherForCloseOpenBracket = patternForCloseOpenBracket.matcher(str);
//
//                if (matcherForCloseOpenBracket.matches()) {
//                    buffer.clear();
//                    buffer.add(str);
//                    continue;
//                }
//
//                Matcher matcherForOpenBracket = patternForOpenBracket.matcher(str);
//
//                if (matcherForOpenBracket.matches()) {
//                    buffer.clear();
//                    buffer.add(str);
//                    continue;
//                }
//
//                Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);
//
//                if (matcherForCloseBracket.matches()) {
//                    buffer.clear();
//                    continue;
//                }
//
//                buffer.add(str);
            }
        }
        catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
    }

    private void searchInFile(String functionName, String pathToFile) {
        logger.setLevel(Level.INFO);

        Pattern patternForFunctionName = Pattern.compile(".*(\\s)(" + functionName + ")(\\().+");
        Pattern patternForOpenBracket = Pattern.compile(".*\\{.*");
        Pattern patternForCloseBracket = Pattern.compile(".*\\}.*");
        Pattern patternForOpenCloseBracket = Pattern.compile(".*\\{.*\\}.*");
        Pattern patternForCloseOpenBracket = Pattern.compile(".*\\}.*\\{.*");

        try {
            FileReader fileReader = new FileReader(pathToFile);
            File file = new File(pathToFile);
            BufferedReader in = new BufferedReader(fileReader);
            String str = "";
            int strNumber = 0;
            List<String> buffer = new ArrayList<>();

            while ((str = in.readLine()) != null) {
                strNumber++;

//                str = strWithNumber(str, strNumber);

                Matcher matcherForFunctionName = patternForFunctionName.matcher(str);

                if (matcherForFunctionName.matches()) {
                    StringBuilder sb = new StringBuilder();
                    for(String s : buffer) {
                        sb.append(s);
                        sb.append(newLine);
//                        sb.append(newLineWithNumber(strNumber));
                    }

                    buffer.clear();

                    sb.append(str);
                    sb.append(newLine);
//                    sb.append(newLineWithNumber(strNumber));

                    str = in.readLine();
                    strNumber++;
//                    str = strWithNumber(str, strNumber);

                    while ((str != null)) {
                        Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);
                        if (matcherForOpenCloseBracket.matches()) {
                            sb.append(str);
                            sb.append(newLine);
//                            sb.append(newLineWithNumber(strNumber));

                            str = in.readLine();
                            strNumber++;
//                            str = strWithNumber(str, strNumber);
                            continue;
                        }
                        Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);
                        if (matcherForCloseBracket.matches()) {
                            sb.append(str);
                            sb.append(newLine);
//                            sb.append(newLineWithNumber(strNumber));
                            break;
                        }
                        sb.append(str);
                        sb.append(newLine);
//                        sb.append(newLineWithNumber(strNumber));
                        str = in.readLine();
                        strNumber++;
//                        str = strWithNumber(str, strNumber);
                    }

                    sb.append(newLine);
//                    sb.append(newLineWithNumber(strNumber));

                    CodeExample codeExample = new CodeExample();
                    codeExample.setLanguage("C");
                    codeExample.setSource(pathToFile + " : " + strNumber);
                    codeExample.setFunction(functionName);
                    codeExample.setCodeExample(sb.toString());
                    codeExample.setModificationDate(file.lastModified());
                    logger.info("Code example parameters: " +
                            "programming lang=" + codeExample.getLanguage() + " " +
                            ", function=" + codeExample.getFunction() + " " +
                            ", source=" + codeExample.getSource() + " " +
                            ", modificationDate" + codeExample.getModificationDate());
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
        } else if (isSourceFileForLanguage("C", pathToFile)) {
                searchInFile(functionName, pathToFile);
        }
    }

    private boolean isSourceFileForLanguage(String language, String pathToFile) {
        if (language.matches("C|c"))
            return pathToFile.endsWith("c");
        else if (language.matches("C\\+\\+|c\\+\\+"))
            return pathToFile.endsWith("cpp");
        else
            return language.matches("java|JAVA|Java") && pathToFile.endsWith("java");
    }

    public List<CodeExample> search(String functionName) {
        searchInDirectory(functionName, startPath);
        return list;
    }
}
