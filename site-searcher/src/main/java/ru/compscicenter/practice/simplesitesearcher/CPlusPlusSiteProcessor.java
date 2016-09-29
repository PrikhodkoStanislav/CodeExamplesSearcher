package ru.compscicenter.practice.simplesitesearcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rodionova Darya on 28.09.2016!
 */
public class CPlusPlusSiteProcessor extends SiteProcessor {
    /**
     * URL to http://www.cplusplus.com/reference/
     */
    private static final String CPLUSPLUS_URL = "http://www.cplusplus.com/reference/";

    @Override
    public List<String> findAndProcessCodeExamples(final String result) {
        List<String> answers = new ArrayList<String>();
        System.out.println("Examples of this method usage..");
        System.out.println("www.cplusplus.com");

        Pattern p = Pattern.compile("<code>((<cite>.*)?(<dfn>.*)?<var>.*)</code>");
        Matcher matcher = p.matcher(result);
        String codeExample;

        while (matcher.find()) {
            codeExample = matcher.group(1);
            codeExample = codeExample.replaceAll("<(/)?(cite|dfn|var|span|kbd)>", "");
            codeExample = codeExample.replaceAll("&gt;", ">");
            codeExample = codeExample.replaceAll("&lt;", "<");

            String prettyCode = super.toPrettyCode(codeExample);
            int intMain = prettyCode.indexOf("int main ()");
            String code = prettyCode.substring(0, intMain)
                + '\n' + prettyCode.substring(intMain);

            answers.add(code);
        }
        return answers;
    }

    //todo search page only by suffix
    public String generateRequestURL(final String query) {
        String[] fullMethodName = query.split("::");
        String requestURL = "";
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                return requestURL;
            }
            if (isVectorContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "vector/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if (isSetContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "set/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if (isUnorderedSetContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "unordered_set/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if (isMapContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "map/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if (isUnorderedMapContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "unordered_map/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if (isQueueContainer(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "queue/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else if ("string".equals(fullMethodName[1])) {
                requestURL = CPLUSPLUS_URL + "string/" + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            } else {
                requestURL = CPLUSPLUS_URL + fullMethodName[1]
                        + "/" + fullMethodName[0] + "/";
            }

        } else {
            String methodName = fullMethodName[fullMethodName.length - 1];
            String structureName = fullMethodName[fullMethodName.length - 2];
            if (isVectorContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "vector/" + structureName
                        + "/" + methodName + "/";
            } else if (isSetContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "set/" + structureName
                        + "/" + methodName + "/";
            } else if (isUnorderedSetContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "unordered_set/" + structureName
                        + "/" + methodName + "/";
            } else if (isMapContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "map/" + structureName
                        + "/" + methodName + "/";
            } else if (isUnorderedMapContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "unordered_map/" + structureName
                        + "/" + methodName + "/";
            } else if (isQueueContainer(structureName)) {
                requestURL = CPLUSPLUS_URL + "queue/" + structureName
                        + "/" + methodName + "/";
            } else if ("string".equals(structureName)) {
                requestURL = CPLUSPLUS_URL + "string/" + structureName
                        + "/" + methodName + "/";
            } else {
                requestURL = CPLUSPLUS_URL + structureName
                        + "/" + methodName + "/";
            }
        }
        return requestURL;
    }

    private boolean isQueueContainer(String s) {
        return "queue".equals(s) || "priority_queue".equals(s);
    }

    private boolean isUnorderedMapContainer(String s) {
        return "unordered_map".equals(s) || "unordered_multimap".equals(s);
    }

    private boolean isMapContainer(String s) {
        return "map".equals(s) || "multimap".equals(s);
    }

    private boolean isUnorderedSetContainer(String s) {
        return "unordered_set".equals(s) || "unordered_multiset".equals(s);
    }

    private boolean isSetContainer(String s) {
        return "set".equals(s) || "multiset".equals(s);
    }

    private boolean isVectorContainer(String s) {
        return "vector".equals(s) || "vector-bool".equals(s);
    }
}
