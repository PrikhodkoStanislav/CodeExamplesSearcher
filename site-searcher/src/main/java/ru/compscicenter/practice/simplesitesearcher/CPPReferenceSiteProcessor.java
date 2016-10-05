package ru.compscicenter.practice.simplesitesearcher;

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

        Pattern p = Pattern.compile("<div class=\"cpp source-cpp\"><pre class=\"de1\">(.*)</pre></div></div><p>");
        Matcher matcher = p.matcher(result);
        String codeExample;
        while (matcher.find()) {
            codeExample = matcher.group(1);
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

            String prettyCode = super.toPrettyCode(codeExample);
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
        String requestURL = "";
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                if (isMathFunction(fullMethodName[0]))
                    requestURL += CPPREFERENCE_URL + "cpp/numeric/math/" + fullMethodName[0];
                else if (isCStringFunction(fullMethodName[0]))
                    requestURL += CPPREFERENCE_URL + "cpp/string/byte/" + fullMethodName[0];
                else if (isAlgorithmFunction(fullMethodName[0]))
                    requestURL += CPPREFERENCE_URL + "cpp/algorithm/" + fullMethodName[0];
                else
                    return requestURL;
            } else
                requestURL = buildURL(fullMethodName[1], fullMethodName[0]);
        } else {
            String methodName = fullMethodName[fullMethodName.length - 1];
            String structureName = fullMethodName[fullMethodName.length - 2];
            requestURL = buildURL(methodName, structureName);
        }
        return requestURL;
    }

    private String buildURL(String methodName, String structureName) {
        String requestURL;
        if (isMathFunction(methodName)) {
            requestURL = CPPREFERENCE_URL + "cpp/numeric/math/" + methodName;
        } else if (isCStringFunction(methodName)) {
                requestURL = CPPREFERENCE_URL + "cpp/string/byte/" + methodName;
        } else if (isAlgorithmFunction(methodName)) {
            requestURL = CPPREFERENCE_URL + "cpp/algorithm/" + methodName;
        } else if (isContainer(structureName)) {
            requestURL = CPPREFERENCE_URL + "cpp/container"
                    + "/" + structureName
                    + "/" + methodName;
        } else if (isAnyStringLibrary(structureName)) {
            requestURL = CPPREFERENCE_URL + "cpp/string"
                    + "/" + structureName
                    + "/" + methodName;
        } else {
            requestURL = CPPREFERENCE_URL + "cpp/io" +
                    "/" + structureName
                    + "/" + methodName;
        }
        return requestURL;
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
