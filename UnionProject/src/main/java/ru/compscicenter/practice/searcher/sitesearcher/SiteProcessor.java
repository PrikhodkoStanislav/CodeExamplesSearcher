package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import ru.compscicenter.practice.searcher.CodeDuplicateRemover;
import ru.compscicenter.practice.searcher.ProjectCodeFormatter;
import ru.compscicenter.practice.searcher.algorithms.AlgorithmsRemoveDuplicates;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by user on 28.09.2016!
 */
public abstract class SiteProcessor extends Thread {
    private final static Logger logger = Logger.getLogger(SiteProcessor.class);

    private Preferences prefs = Preferences.userRoot().node("settings");
    private final static int defaultMaxExamplesNumber = 20;
    private final static int defaultDuplicator = 1;

    private String language;
    private String query;
    private List<CodeExample> answers;
    private Pattern p;

    @Override
    public void run() {
        logger.setLevel(Level.INFO);

        answers = new ArrayList<>();
        String request = generateRequestURL(getQuery());
        if (request != null && !"".equals(request)) {
            try {
                String webContent = sendGet(request.trim());
                if (!webContent.contains("Page Not Found")) {
                    List<CodeExamplesWithSource> codeSourceList = findAndProcessCodeExamples(webContent);

                    ProjectCodeFormatter projectCodeFormatter = new ProjectCodeFormatter();
                    List<CodeExample> prepareExamples = null;
                    if (codeSourceList != null) {
                        prepareExamples = new ArrayList<>();
                        prepareExamples.addAll(extractCodeAndFindExamples(codeSourceList));


                        projectCodeFormatter.beautifyCode(prepareExamples);

                        int duplicator = prefs.getInt("duplicator", defaultDuplicator);
                        if (duplicator == 1) {
                            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.LevenshteinDistance;
                            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(prepareExamples, typeOfCompareResult);
                            prepareExamples = duplicateRemover.removeDuplicates();
                        } else if (duplicator == 2) {
                            AlgorithmsRemoveDuplicates typeOfCompareResult = AlgorithmsRemoveDuplicates.EqualsTokens;
                            CodeDuplicateRemover duplicateRemover = new CodeDuplicateRemover(prepareExamples, typeOfCompareResult);
                            prepareExamples = duplicateRemover.removeDuplicates();
                        }
                        int maxExamplesNumber = prefs.getInt("maxExamplesNumber", defaultMaxExamplesNumber);
                        int size = prepareExamples.size();
                        if (size > maxExamplesNumber) {
                            prepareExamples.removeAll(prepareExamples.subList(maxExamplesNumber, size));
                        }
                    }
                    answers.addAll(prepareExamples);
                }
            } catch (Exception e) {
                logger.error("Sorry, something wrong!", e);
            }
        }
    }

    /**
     * Extracts code and find examples by function
     * @param codeSourceList web-source with text without html-tags
     * @return list of code examples
     * */
    private List<CodeExample> extractCodeAndFindExamples(List<CodeExamplesWithSource> codeSourceList) {
        List<CodeExample> results = new ArrayList<>();
        for (CodeExamplesWithSource codeWithSource : codeSourceList) {
            codeWithSource.body = removeComments(codeWithSource.body);
            searchInFileAllFunction(getQuery(), codeWithSource, results);
        }
        return results;
    }

    private String removeComments(String body) {
    StringBuilder sb = new StringBuilder();
        String[] lines = body.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("/*") || line.matches("\\s+/\\*.*")) {
                while (!lines[i].endsWith("*/")) {
                    i++;
                }
                i++;
                line = lines[i];
            }

            while (i < lines.length || lines[i].startsWith("//") || lines[i].endsWith(".") ||
                    lines[i].matches("\\s*\\d+") || lines[i].matches("[\\s\\t\\r]+") ||
                    lines[i].matches("\\s*\\w[\\s\\w]*")) {
                i++;
            }

            if (i < line.length()) {
                sb.append(line).append("\n");
            }
        }
        String result = sb.toString();
        result = result.replaceAll("\n\n+", "\n\n");
        return result;
    }

    private void searchInFileAllFunction(String functionName, CodeExamplesWithSource codeWithSource, List<CodeExample> results) {
        InputStream is = new ByteArrayInputStream((codeWithSource.body).getBytes(StandardCharsets.UTF_8));

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String str = "";
            long strNumber = 0;
            long lineWithFunction = 0;

            List<String> buffer = new ArrayList<>();

            long countBrackets = 0;

            while ((str = in.readLine()) != null) {
                strNumber++;

                countBrackets += numberBrackets(str);

                if ((str.contains(" " + functionName + "(") || str.contains("=" + functionName + "(") ||
                        str.contains("(" + functionName + "(") || str.contains("\t" + functionName + "(")) &&
                        (!str.endsWith(")") && !str.contains(functionName + "(const") &&
                                !str.contains(functionName + "( const")) && !isNaturalSentence(str)) {

                    StringBuilder sb = new StringBuilder();
                    String newLine = "\n";
                    for(String s : buffer) {
                        sb.append(s);
                        sb.append(newLine);
                    }

                    buffer.clear();

                    sb.append(str);
                    sb.append(newLine);
                    lineWithFunction = strNumber;
                    while ((countBrackets != 0) && (str = in.readLine()) != null) {
                        strNumber++;
                        countBrackets += numberBrackets(str);
                        sb.append(str);
                        sb.append(newLine);
                    }
//                    sb.append(newLine);

                    CodeExample codeExample = new CodeExample();
                    codeExample.setLanguage("C");
                    codeExample.setSource(codeWithSource.source);
                    codeExample.setLineWithFunction(lineWithFunction);
                    codeExample.setFunction(functionName);
                    codeExample.setCodeExample(sb.toString());
                    codeExample.setModificationDate(new Date().getTime());
                    logger.info("Code example parameters: " +
                            "programming lang=" + codeExample.getLanguage() + " " +
                            ", function=" + codeExample.getFunction() + " " +
                            ", source=" + codeExample.getSource() + " " +
                            ", modificationDate=" + codeExample.getModificationDate());
                    results.add(codeExample);

                    int defaultMaxExamplesNumber = 20;
                    int maxExamplesNumber = defaultMaxExamplesNumber;
                    if (answers.size() >= maxExamplesNumber) {
                        break;
                    }

                } else if (countBrackets == 0) {
                    buffer.clear();
                }
                // Always have string before construction with correct sequence of brackets.
                buffer.add(str);
//                } else {
//                    buffer.add(str);
//                }
            }
        }
        catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
    }

    private boolean isNaturalSentence(String line) {
        if (line.contains("//")) {
            return false;
        }
        String[] tokens = line.split(" ");
        int len = tokens.length;
        int a = 0;
        for (String token : tokens) {
            if (isNotNatural(token)) {
                a++;
            }
        }
        double c = (double) a / len;
        return c >= 0.5;
    }

    private boolean isNotNatural(String token) {
        return token.matches("[\\+\\-\\\\*/=\\(\\)<>\\{\\}\\.;,]") ||
            token.matches("(str|var|new|null|NULL|nullptr|" +
                    "char|float|byte|short|double|int|const|void|" +
                    "if|else|for|while|switch)") ||
            token.contains("_") || isCamelCase(token) ||
                (token.contains("+") || token.contains("-") ||
                token.contains("*") || token.contains("/") ||
                token.contains("=") || token.contains(";") ||
                token.contains(".") || token.contains(",") ||
                token.contains("(") || token.contains(")") ||
                token.contains("<") || token.contains(">") ||
                token.contains("{") || token.contains("}") ||
                token.contains("[") || token.contains("]"));
    }

    private boolean isCamelCase(String token) {
        if (Character.isUpperCase(token.charAt(0))) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (Character.isUpperCase(token.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private long numberBrackets(String str) {
        long result = 0;
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (c == '{') {
                result++;
            } else if (c == '}') {
                result--;
            }
        }
        return result;
    }

    /**
     * Set HTTP-query to the site and get HTML or JSON content
     * @param url we-address of the search site
     * @return web-site content if response code in (200, 201), otherwise failed message
     * */
    public String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        Map<String, List<String>> headers = con.getHeaderFields();

        logger.info("Sending HTTP-request to url: " + url +
                " with response code: " + con.getResponseCode());

        for (String header : headers.get(null)) {
            if (header.contains(" 302 ") || header.contains(" 301 ")) {
                url = headers.get("Location").get(0);
                obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();
                headers = con.getHeaderFields();
            }
        }

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is;
            BufferedReader in;
            if ("gzip".equals(con.getContentEncoding())) {
                is = new GZIPInputStream(con.getInputStream());
            }
            else {
                is = con.getInputStream();
            }
            in = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if (!url.contains("api")) {
                    response.append("\n");
                }
            }
            in.close();
            con.disconnect();

            return response.toString();
        } else {
            return "Page Not Found";
        }
    }

    protected String cleanTextFromHTMlTags(String html) throws TikaException, SAXException, IOException {
        InputStream input = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        new HtmlParser().parse(input, handler, metadata);
        html = handler.toString();
        return html;
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
     * */
    public abstract List<CodeExamplesWithSource> findAndProcessCodeExamples(final String result);

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
        p = Pattern.compile("[\\s\\t\\+\\-\\*/=\\(]" + query + "\\s?\\(");
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<CodeExample> getAnswers() {
        return answers;
    }

    /**
     * These regular expressions belong to API for cppreference.com and cplusplus.com
     * @param s function name
     * return true if function name matches one of the regexp class, otherwise false
     **/
    protected boolean isMathFunction(String s) {
        return s.matches("(a?(sin|cos|tan)h?|atan2|" +
                "l?|l?(abs|mod|round|rint|max|min)|" +
                "f?(abs|mod|dim|round|rint|max|min)|" +
                "div|pow|sqrt|nan(f|l)?|cbrt|hypot|" +
                "ceil|floor|trunc|modf|(fr|ld)exp|" +
                "is(inf|finite|nan|normal|(greater|less)(equal)?)" +
                "exp(2|ml)?|log(2|10|1p)?)");
    }

    protected boolean isCStdIOFunction(String s) {
        return s.matches("(f((re)?open|close|flush|wide|read|write)|setv?buf|" +
                "(f|un)?(get|put)(w?(c|char|s))|" +
                "v?(f|sn?)?w?(scan|print)f(_s)?|" +
                "f((get|set)pos|seek|tell)|re(wind|move|name)|" +
                "clearerr|(f|p)e(rror|of)|tmp(file|nam))");
    }

    protected boolean isAlgorithmFunction(String s) {
        return s.matches("(qsort|bsearch|binary_search|equal_range|(lower|upper)_bound|" +
                "(min|max|minmax)(_element)?|nth_element|" +
                "((partial|stable|is)_)?sort(_copy|ed(_untill)?)?)");
    }

    protected boolean isCStringFunction(String s) {
        return s.matches("(w?mem(chr|cmp|(cpy|move|set)(_s)?)|" +
                "(str|wcs)(r?chr|((n?c(at|py)|error(len)?|len|tok)(_s)?)|n?cmp|coll|c?spn|pbrk|str))");
    }

    protected boolean isCMultyStringFunction(String s) {
        return s.matches("(mbr?(len|towc)|mbsr?towcs(_s)?|mbsinit|btowc|" +
                "wctob|wcr?tomb(_s)?|wcsr?tombs(_s)?)");
    }

    protected boolean isCUnicodeCharFunction(String s) {
        return s.matches("(mbrtoc(16|32)|c(16|32)rtomb)");
    }

    protected boolean isCStdLibFunction(String s) {
        return s.matches("(ato(ll?|f|i)?|(str|wcs)to(u?ll?|f|l?d|(i|u)max)|s?rand)");
    }

    protected boolean isCTypeFunction(String s) {
        return s.matches("isw?(al(num|pha)|(low|upp)er|blank|cntrl|x?digit|graph|space|p(rint|unct))|" +
                "tow?(low|upp)er|(is|to)?wc(type|trans)");
    }

    protected boolean isCAlgorithmFunction(String s) {
        return s.matches("(bsearch|qsort)(_s)?");
    }

    protected boolean isCAssert(String s) {
        return s.matches("assert|errno");
    }

    protected boolean isCMemory(String s) {
        return s.matches("(c|m|re|aligned_)alloc|free");
    }

    protected boolean isCFenv(String s) {
        return s.matches("(fe(clear|hold|raise|test)except)|" +
                "fe((g|s)et)(env|exceptflag|round)");
    }

    protected boolean isCEnvironment(String s) {
        return s.matches("(abort|_Exit|(at_)?(quick_)?exit|" +
                "atexit|getenv(_s)?|system)");
    }

    protected boolean isCSignal(String s) {
        return s.matches("(signal|raise)");
    }

    private class AnswerLine {
        private String line;
        private boolean isCode;

        public AnswerLine(String line, boolean isCode) {
            this.line = line;
            this.isCode = isCode;
        }
    }

    protected class CodeExamplesWithSource {
        protected String source;
        protected String body;
        protected List<String> codeFragments;

        protected CodeExamplesWithSource(String source, String body) {
            this.source = source;
            this.body = body;
        }
    }
}
