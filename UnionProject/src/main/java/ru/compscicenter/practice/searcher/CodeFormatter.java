package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by user on 06.10.2016!
 */
public class CodeFormatter {

    public void createResultFile(List<CodeExample> examples, String format) {
        if ("html".equals(format))
            createHtml(examples);
        else if ("txt".equals(format))
            createTxt(examples);
        else
            printResults(examples);
    }

    public void createHtml(List<CodeExample> examples) {
        String path = "examples.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("<html>");
            writer.write("<h3 align=\"center\">Code examples from sites</h3>");
            writer.write("<body>");
            for (CodeExample example : examples) {
                writer.write("<pre>" + example.toString("html") + "</pre><br>");
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
            writer.write("==============================================================================\n");
            writer.write("||                       Code examples from sites:                          ||\n");
            writer.write("==============================================================================\n");
            for (CodeExample example : examples) {
                writer.write("==============================================================================\n");
                String code = example.getCodeExample();
                code = code.replaceAll("&lt;", "<");
                code = code.replaceAll("&gt;", ">");
                example.setCodeExample(code);
                writer.write(example.toString("txt"));
                writer.write("==============================================================================\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printResults(List<CodeExample> examples) {
        System.out.print("==============================================================================\n");
        System.out.print("||                       Code examples from sites:                          ||\n");
        System.out.print("==============================================================================\n");
        for (CodeExample example : examples) {
            System.out.print("==============================================================================\n");
            String code = example.getCodeExample();
            code = code.replaceAll("&lt;", "<");
            code = code.replaceAll("&gt;", ">");
            example.setCodeExample(code);
            System.out.print(example.toString("txt"));
            System.out.print("==============================================================================\n");
        }
    }

    public void beautifyCode(List<CodeExample> l1) {
        for (CodeExample codeExample : l1) {
            codeExample.setCodeExample(toPrettyCode(codeExample.codeExample));
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
        return intMain > 0 ? (prettyCode.substring(0, intMain) +
                "\n" + prettyCode.substring(intMain)) : prettyCode;
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
