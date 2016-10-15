package ru.compscicenter.practice.searcher.selfprojectsearcher;

import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.codeexample.SelfProjectCodeExample;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Станислав on 05.10.2016.
 */
public class SelfProjectSearcher extends Searcher {

    public List<CodeExample> list = new ArrayList<>();

    private String makeResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>").append("<br>")
                .append("<html>").append("<br>");
        sb.append("<h3>Examples of this method usage from projects:</h3>").append("<br>");
        sb.append("<body>");
        List<CodeExample> answers = list;
        if (answers != null)
            for (CodeExample answer : answers) {
                sb.append("<div clas=\"pretty\">").append(answer.toString()).append("</div>").append("<br>");
            }
        else
            sb.append("<p>Sorry! Connection was interrupted! :(<p>").append("<br>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    @Override
    public String search(String functionName, String pathToFile) {
        final String newLine = "\n";

        Pattern patternForFunctionName = Pattern.compile(".*(\\s)(" + functionName + ")(\\().+");
        Pattern patternForOpenBracket = Pattern.compile(".*\\{.*");
        Pattern patternForCloseBracket = Pattern.compile(".*\\}.*");
        Pattern patternForOpenCloseBracket = Pattern.compile(".*\\{.*\\}.*");
        Pattern patternForCloseOpenBracket = Pattern.compile(".*\\}.*\\{.*");

        File file = new File(pathToFile);
        if (!file.exists()) {
            System.out.println("Wrong path to the file!");
//            return;
            return "";
        }
        if (file.isDirectory()) {
            File[] filesInDirectory = file.listFiles();
            for (File f : filesInDirectory) {
//                String res = search(functionName, f.getPath());
                search(functionName, f.getPath());
//                if ((res.length() > 0) && f.isFile()) {
////                    sb.append(f.getPath() + newLine);
////                    sb.append(search(functionName, f.getPath()));
//                }
            }
            return makeResult();
//            return "";
//            return;
//            return sb.toString();
        }

        try {
            FileReader fileReader = new FileReader(pathToFile);
            BufferedReader in = new BufferedReader(fileReader);
            String str;
            int numberOfExample = 0;
            int strNumber = 0;
            List<String> buffer = new LinkedList<>();

            while ((str = in.readLine()) != null) {
                strNumber++;
                Matcher matcherForFunctionName = patternForFunctionName.matcher(str);

                if (matcherForFunctionName.matches()) {
                    StringBuilder sb = new StringBuilder();
                    numberOfExample++;
//                    sb.append("Example " + numberOfExample + " :" + " str " + strNumber + " :" + newLine);

                    for(String s : buffer) {
                        sb.append(s + newLine);
                    }

                    buffer.clear();

                    sb.append(str + newLine);
                    str = in.readLine();

                    while ((str != null)) {
                        Matcher matcherForOpenCloseBracket = patternForOpenCloseBracket.matcher(str);
                        if (matcherForOpenCloseBracket.matches()) {
                            sb.append(str + newLine);
                            str = in.readLine();
                            continue;
                        }
                        Matcher matcherForCloseBracket = patternForCloseBracket.matcher(str);
                        if (matcherForCloseBracket.matches()) {
                            sb.append(str + newLine);
                            break;
                        }
                        sb.append(str + newLine);
                        str = in.readLine();
                    }

                    sb.append(newLine);
                    CodeExample codeExample = new SelfProjectCodeExample(pathToFile,
                            numberOfExample, strNumber, sb.toString());
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
            e.printStackTrace();
        }
        return makeResult();
//        return "";
//        return sb.toString();
    }
}
