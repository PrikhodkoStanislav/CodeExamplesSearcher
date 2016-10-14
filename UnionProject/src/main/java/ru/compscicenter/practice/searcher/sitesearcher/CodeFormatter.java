package ru.compscicenter.practice.searcher.sitesearcher;

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

    public String toPrettyCode(String code) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char symbol = code.charAt(i);
            if (symbol == '#') {
                sb.append("<br>").append(symbol);
            } else if (symbol == ';') {
                if (isNotEmptyAfterSemicolon(code, i)) {
                    sb.append(symbol);
                } else {
                    sb.append(symbol).append("<br>");
                }
            } else if (symbol == ')') {
                if (isBeforeNewLine(code, i)) {
                    sb.append(symbol).append("<br>");
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
                    sb.append(symbol).append("<br>");
            } else {
                sb.append(symbol);
            }
        }

        String prettyCode = sb.toString();
        int intMain = prettyCode.indexOf("int main ()");
        if (intMain < 0)
            intMain = prettyCode.indexOf("int main()");
        String result = intMain > 0 ? (prettyCode.substring(0, intMain) +
                "<br>" + prettyCode.substring(intMain)) : prettyCode;
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
