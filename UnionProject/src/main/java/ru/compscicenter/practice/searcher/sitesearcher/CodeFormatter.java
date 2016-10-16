package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by user on 06.10.2016!
 */
public class CodeFormatter {

    private static CodeFormatter instance;

    private CodeFormatter() {}

    public static CodeFormatter getInstance() {
        if (instance == null)
            instance = new CodeFormatter();
        return instance;
    }

    public void createHTML(List<CodeExample> examples) {
        String path = "examples.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("<html>");
            writer.write("<h3>Code examples from sites</h3>");
            writer.write("<body>");
            for (CodeExample example : examples) {
                writer.write("<pre>" + example.toString() + "</pre><br>");
            }
            writer.write("</body>");
            writer.write("</html>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTxt(List<CodeExample> examples) {
        String path = "examples.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("Code examples from sites:");
            for (CodeExample example : examples) {
                String code = example.getCodeExample();
                code = code.replaceAll("&lt;", "<");
                code = code.replaceAll("&gt;", ">");
                example.setCodeExample(code);
                writer.write(example.toString());
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toPrettyCode(String code) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char symbol = code.charAt(i);
            if (symbol == '#') {
                sb.append("\n").append(symbol);
            } else if (symbol == ';') {
                if (isNotEmptyAfterSemicolon(code, i)) {
                    sb.append(symbol);
                } else {
                    sb.append(symbol).append("\n");
                }
            } else if (symbol == ')') {
                if (isBeforeNewLine(code, i)) {
                    sb.append(symbol).append("\n");
                } else {
                    sb.append(symbol);
                }
            /*} else if (symbol == ' ') {
                if ((i + 2) < code.length() && isNoSpaceChar(code, i + 2)) {
                    sb.append("\n").append(symbol);
                } else {
                    sb.append(symbol);
                }*/
            } else if (symbol == '{' || symbol == '}') {
                if ((i + 2) < code.length() && (isNoSpaceChar(code, i + 1) || isNoSpaceChar(code, i + 2)))
                    sb.append(symbol);
                else
                    sb.append(symbol).append("\n");
            } else {
                sb.append(symbol);
            }
        }

        String prettyCode = sb.toString();
        int intMain = prettyCode.indexOf("int main ()");
        if (intMain < 0)
            intMain = prettyCode.indexOf("int main()");
        String result = intMain > 0 ? (prettyCode.substring(0, intMain) +
                "\n" + prettyCode.substring(intMain)) : prettyCode;
        return result;
    }

    private boolean isBeforeNewLine(String code, int i) {
        return (i + 2) < code.length() && ((code.charAt(i + 1) != '{' &&
                code.charAt(i + 1) != ')' && code.charAt(i + 1) != ',' && code.charAt(i + 1) != ';')
                || ((code.charAt(i + 1) == ' ' && (code.charAt(i + 2) != '{'
                && Character.isLetter(code.charAt(i + 2))))));
    }

    private boolean isNotEmptyAfterSemicolon(String code, int i) {
        return (i + 2) < code.length() && isNoSpaceChar(code, i + 2) && isNoSpaceChar(code, i + 1);
    }

    private boolean isNoSpaceChar(String code, int i) {
        return Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '-'
                || code.charAt(i) == '*' || code.charAt(i) == '/'
                || code.charAt(i) == '+' || code.charAt(i) == ';'
                || code.charAt(i) == '<' || code.charAt(i) == '>';
    }
}
