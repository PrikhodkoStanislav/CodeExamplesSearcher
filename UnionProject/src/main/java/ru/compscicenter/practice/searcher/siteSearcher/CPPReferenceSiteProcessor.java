package ru.compscicenter.practice.searcher.siteSearcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rodionova Darya on 28.09.2016!
 */
public class CPPReferenceSiteProcessor extends SiteProcessor {
    /**
     * URL to http://en.cppreference.com/w/
     */
    private static final String CPPREFERENCE_URL = "http://en.cppreference.com/w/";

    @Override
    public List<String> findAndProcessCodeExamples(final String result) {
        List<String> answers = new ArrayList<>();

        Pattern p = Pattern.compile("<div class=\"t-example\">.*<div class=\"c(pp)? source-c(pp)?\"><pre class=\"de1\">(.*)</pre></div></div><p>");
        Matcher matcher = p.matcher(result);
        String codeExample;
        while (matcher.find()) {
            codeExample = matcher.group(3);
            codeExample = codeExample
                .replaceAll(
                        "<(/)?(span|a)(\\s((class=\"[a-z]{2}\\d+\")|(href=\"https?://[a-zA-Z\\.]([_a-zA-Z\\./])*\")))?>"
                        , ""
                );
            codeExample = codeExample.replaceAll("&#40;", "(");
            codeExample = codeExample.replaceAll("&#41;", ")");
            codeExample = codeExample.replaceAll("&#91;", "[");
            codeExample = codeExample.replaceAll("&#93;", "]");
            codeExample = codeExample.replaceAll("&#123;", "{");
            codeExample = codeExample.replaceAll("&#125;", "}");
            codeExample = codeExample.replaceAll("&#160;", " ");
            codeExample = codeExample.replaceAll("&gt;", ">");
            codeExample = codeExample.replaceAll("&lt;", "<");
            codeExample = codeExample.replaceAll("&quot;", "\"");
            codeExample = codeExample.replaceAll("&amp;", "&");

            String prettyCode = toPrettyCode(codeExample);
            int intMain = prettyCode.indexOf("int main ()");
            if (intMain < 0)
                intMain = prettyCode.indexOf("int main()");
            String code = intMain > 0 ? (prettyCode.substring(0, intMain) +
                '\n' + prettyCode.substring(intMain)) : prettyCode;

            answers.add(code);
        }
        return answers;
    }

    @Override
    public String getSiteName() {
        return CPPREFERENCE_URL.substring(0, CPPREFERENCE_URL.length() - 2);
    }

    @Override
    public String generateRequestURL(final String query) {
        String[] fullMethodName = query.split("::");
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                if (isMathFunction(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/numeric/math/" + fullMethodName[0];
                else if (isCAssert(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/error/" + fullMethodName[0];
                else if (isCStringFunction(fullMethodName[0]) || isCStdLibFunction(fullMethodName[0]) || isCTypeFunction(fullMethodName[0])) {
                    fullMethodName[0] = fullMethodName[0].replaceAll("_s$", "");
                    fullMethodName[0] = fullMethodName[0].replaceAll("errorlen$", "error");
                    return getStdLibUrl(fullMethodName[0]);
                } else if (isCStdIOFunction(fullMethodName[0]))
                    return getStdIOUrl(fullMethodName[0]);
                else if (isAlgorithmFunction(fullMethodName[0]))
                    return CPPREFERENCE_URL + "cpp/algorithm/" + fullMethodName[0];
                else
                    return "";
            } else
                return buildURL(fullMethodName[1], fullMethodName[0]);
        } else {
            String methodName = fullMethodName[fullMethodName.length - 1];
            String structureName = fullMethodName[fullMethodName.length - 2];
            return buildURL(methodName, structureName);
        }
    }

    private String buildURL(String methodName, String structureName) {
        if (isMathFunction(methodName)) {
            return CPPREFERENCE_URL + "c/numeric/math/" + methodName;
        } else if (isCAssert(methodName)) {
            return CPPREFERENCE_URL + "c/error/" + methodName;
        } else if (isCStringFunction(methodName) || isCStdLibFunction(methodName) || isCTypeFunction(methodName)) {
            methodName = methodName.replaceAll("_s$", "");
            methodName = methodName.replaceAll("errorlen$", "error");
            return getStdLibUrl(methodName);
        } else if (isCStdIOFunction(methodName)) {
            return getStdIOUrl(methodName);
        } else if (isAlgorithmFunction(methodName)) {
            return CPPREFERENCE_URL + "cpp/algorithm/" + methodName;
        } else if (isContainer(structureName)) {
            return CPPREFERENCE_URL + "cpp/container"
                    + "/" + structureName
                    + "/" + methodName;
        } else if (isAnyStringLibrary(structureName)) {
            return CPPREFERENCE_URL + "cpp/string"
                    + "/" + structureName
                    + "/" + methodName;
        } else {
            return CPPREFERENCE_URL + "cpp/io" +
                    "/" + structureName
                    + "/" + methodName;
        }
    }

    private String getStdLibUrl(String methodName) {
        if (methodName.matches("ato(ll?|i)?"))
            return CPPREFERENCE_URL + "c/string/byte/atoi";
        else if (methodName.matches("atof"))
            return CPPREFERENCE_URL + "c/string/byte/atof";
        else if (methodName.matches("strto(ll?)"))
            return CPPREFERENCE_URL + "c/string/byte/strtol";
        else if (methodName.matches("strtoll?"))
            return CPPREFERENCE_URL + "c/string/byte/strtoul";
        else if (methodName.matches("strto(f|l?d)"))
            return CPPREFERENCE_URL + "c/string/byte/strtod";
        else
            return CPPREFERENCE_URL + "c/string/byte/" + methodName;
    }

    private String getStdIOUrl(String methodName) {
        if (methodName.matches("(f|s)?scanf"))
            return CPPREFERENCE_URL + "cpp/io/c/fscanf";
        else if (methodName.matches("v(f|s)?scanf"))
            return CPPREFERENCE_URL + "cpp/io/c/vfscanf";
        else if (methodName.matches("(f|sn?)?printf"))
            return CPPREFERENCE_URL + "cpp/io/c/fprintf";
        else if (methodName.matches("v(f|sn?)?printf"))
            return CPPREFERENCE_URL + "cpp/io/c/vfprintf";
        else if (methodName.matches("(f|s)?wscanf"))
            return CPPREFERENCE_URL + "cpp/io/c/fwscanf";
        else if (methodName.matches("v(f|s)?wscanf"))
            return CPPREFERENCE_URL + "cpp/io/c/vfwscanf";
        else if (methodName.matches("(f|s)?wprintf"))
            return CPPREFERENCE_URL + "cpp/io/c/fwprintf";
        else if (methodName.matches("v(f|s)?wprintf"))
            return CPPREFERENCE_URL + "cpp/io/c/vfwprintf";
        else if (methodName.matches("f?putc"))
            return CPPREFERENCE_URL + "cpp/io/c/fputc";
        else if (methodName.matches("f?getc"))
            return CPPREFERENCE_URL + "cpp/io/c/fgetc";
        else if (methodName.matches("f?putwc"))
            return CPPREFERENCE_URL + "cpp/io/c/fputwc";
        else if (methodName.matches("f?getwc"))
            return CPPREFERENCE_URL + "cpp/io/c/fgetwc";
        else
            return CPPREFERENCE_URL + "cpp/io/c/" + methodName;
    }

    private boolean isAnyStringLibrary(String s) {
        return "basic_string".equals(s) || "basic_string_view".equals(s);
    }

    private boolean isContainer(String s) {
        return "vector".equals(s) || "stack".equals(s) || "deque".equals(s)
            || "queue".equals(s) || "priority_queue".equals(s)
            || "list".equals(s) || "forward_list".equals(s)
            || "set".equals(s) || "multiset".equals(s)
            || "unordered_set".equals(s) || "unordered_multiset".equals(s)
            || "map".equals(s) || "multimap".equals(s)
            || "unordered_map".equals(s) || "unordered_multimap".equals(s);
    }
}
