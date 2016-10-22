package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by user on 06.10.2016!
 */
public class CodeFormatter {

    public String createResultFile(List<CodeExample> examples, String format) {
        if ("html".equals(format))
            return createHtml(examples);
        else
            return createTxt(examples);
    }

    public String createHtml(List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h3 align=\"center\">Code examples from sites</h3>");
        sb.append("<body>");
        for (CodeExample example : examples) {
            sb.append("<pre>").append(example.toString("html")).append("</pre><br>");
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public String createTxt(List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================================\n");
        sb.append("||                       Code examples from sites:                          ||\n");
        sb.append("==============================================================================\n");
        for (CodeExample example : examples) {
            sb.append("==============================================================================\n");
            String code = example.getCodeExample();
            code = code.replaceAll("&lt;", "<");
            code = code.replaceAll("&gt;", ">");
            example.setCodeExample(code);
            sb.append(example.toString("txt"));
            sb.append("==============================================================================\n");
        }
        return sb.toString();
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
