package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.CodeExamplesStorage;
import ru.compscicenter.practice.searcher.codeexample.SiteCodeExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 28.09.2016!
 */
public abstract class SiteProcessor extends Thread {
    private String query;
    private CodeFormatter codeFormatter = CodeFormatter.getInstance();

    @Override
    public void run() {
        String request = generateRequestURL(getQuery());
        if (request == null || "".equals(request)) {
            System.out.print("Please, exact your function name");
        } else {
            try {
                String webContent = sendGet(request);
                if (webContent.contains("Page Not Found")) {
                    CodeExamplesStorage.getInstance().addCodeExample(
                            new SiteCodeExample(getSiteName(), "No such method found!"));
                } else {
                    findAndProcessCodeExamples(webContent);
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
     * */
    public abstract void findAndProcessCodeExamples(final String result);

    /**
     * Find and process search results (remove extra tags and spans)
     * and then make code examples pretty
     * @return name of site
     * */
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
        if (responseCode == 404)
            return "Page Not Found";

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

    public String toPrettyCode(String code) {
        return codeFormatter.toPrettyCode(code);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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
        return s.matches("(ato(ll?|f|i)?|(str|wcs)to(u?ll?|f|l?d|(i|u)max))");
    }

    protected boolean isCTypeFunction(String s) {
        return s.matches("isw?(al(num|pha)|(low|upp)er|blank|cntrl|x?digit|graph|space|p(rint|unct))|" +
                "tow?(low|upp)er|(is|to)?wc(type|trans)");
    }

    protected boolean isCAlgorithmFunction(String s) {
        return s.matches("(bsearch|qsort)(_s)?");
    }

    protected boolean isCAssert(String s) {
        return s.matches("assert");
    }

    protected boolean isCMemory(String s) {
        return s.matches("(c|m|re|aligned_)alloc|free");
    }
}