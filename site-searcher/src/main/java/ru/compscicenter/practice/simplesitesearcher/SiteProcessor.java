package ru.compscicenter.practice.simplesitesearcher;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 28.09.2016!
 */
public abstract class SiteProcessor extends Thread {
    private List<String> answers;
    private String query;

    @Override
    public void run() {
        String request = generateRequestURL(getQuery());
        if (request == null || "".equals(request)) {
            answers = new ArrayList<>();
            answers.add("Please, exact yor function name");
        } else {
            try {
                String webContent = sendGet(request);
                if (webContent.contains("Page Not Found")) {
                    answers = new ArrayList<>();
                    answers.add("No such method found!");
                } else {
                    answers = findAndProcessCodeExamples(webContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate Request URL
     * @param query - user's query
     * @return url
     * */
    public abstract String generateRequestURL(final String query);

    /**
     * Find and process search results (remove extra tags and spans)
     * and then make code examples pretty
     * @param result - finding html page
     * @return list with code examples
     * */
    public abstract List<String> findAndProcessCodeExamples(final String result);

    public abstract String getSiteName();

    public String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        String USER_AGENT = "Mozilla/5.0";
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();
        return response.toString();
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    protected String toPrettyCode(String code) {
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
        return sb.toString();
    }

    private boolean isBeforeNewLine(String code, int i) {
        return (i + 2) < code.length() && ((code.charAt(i + 1) != '{' && code.charAt(i + 1) != ')' && code.charAt(i + 1) != ';')
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

    protected boolean isMathFunction(String s) {
        return s.matches("(a?(sin|cos|tan)h?|atan2|" +
                "l?|l?(abs|mod|round|rint|max|min)|" +
                "f?(abs|mod|dim|round|rint|max|min)|" +
                "div|pow|sqrt|nan(f|l)?|cbrt|hypot|" +
                "ceil|floor|trunc|modf|(fr|ld)exp|" +
                "is(inf|finite|nan|normal|(greater|less)(equal)?)" +
                "exp(2|ml)?|log(2|10|1p)?)");
    }

    protected boolean isAlgorithmFunction(String s) {
        return s.matches("(qsort|bsearch|binary_search|equal_range|(lower|upper)_bound|" +
                "(min|max|minmax)(_element)?|nth_element|" +
                "((partial|stable|is)_)?sort(_copy|ed(_untill)?)?)");
    }

    protected boolean isCStringFunction(String s) {
        return s.matches("(mem(chr|cmp|cpy|move|set)|" +
                "str(r?chr|n?cat|n?cmp|coll|n?cpy|c?spn|error|len|pbrk|str|tok))");
    }
}
