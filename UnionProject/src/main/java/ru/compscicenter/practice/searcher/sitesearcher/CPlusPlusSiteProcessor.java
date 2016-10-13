package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.CodeExamplesStorage;

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
    public void findAndProcessCodeExamples(final String result) {
        
        Pattern p = Pattern.compile("Example.*<code>((<cite>.*)?(<dfn>.*)?<var>.*})</code>");
        Matcher matcher = p.matcher(result);
        String codeExample;
        while (matcher.find()) {
            codeExample = matcher.group(1);
            codeExample = codeExample.replaceAll("<(/)?(cite|dfn|var|span|kbd)>", "");
            codeExample = codeExample.replaceAll("&gt;", ">");
            codeExample = codeExample.replaceAll("&lt;", "<");

            String prettyCode = toPrettyCode(codeExample);
            int intMain = prettyCode.indexOf("int main ()");
            if (intMain < 0)
                intMain = prettyCode.indexOf("int main()");
            String code = prettyCode.substring(0, intMain)
                + '\n' + prettyCode.substring(intMain);

            CodeExamplesStorage.getInstance().addCodeExample(code);
        }
    }

    @Override
    public String getSiteName() {
        return CPLUSPLUS_URL.substring(0, CPLUSPLUS_URL.length() - 10);
    }

    public String generateRequestURL(final String query) {
        String[] fullMethodName = query.split("::");
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                fullMethodName[0] = fullMethodName[0].replaceAll("_s$", "");
                if (isMathFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cmath/" + fullMethodName[0] + "/";
                else if (isCAssert(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cassert/" + fullMethodName[0] + "/";
                else if (isCMemory(fullMethodName[0]) || isCAlgorithmFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cstdlib/" + fullMethodName[0] + "/";
                else if (isCUnicodeCharFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cuchar/" + fullMethodName[0] + "/";
                else if (isCStringFunction(fullMethodName[0])) {
                    fullMethodName[0] = fullMethodName[0].replaceAll("errorlen$", "error");
                    if (fullMethodName[0].startsWith("wmem") || fullMethodName[0].startsWith("wcs"))
                        return CPLUSPLUS_URL + "cwchar/" + fullMethodName[0] + "/";
                    else
                        return CPLUSPLUS_URL + "cstring/" + fullMethodName[0] + "/";
                } else if (isCMultyStringFunction(fullMethodName[0])) {
                    if (fullMethodName[0].startsWith("wcr") || fullMethodName[0].startsWith("wcsr") ||
                            fullMethodName[0].startsWith("mbr") || fullMethodName[0].startsWith("mbsr") ||
                            "btowc".equals(fullMethodName[0]) || "mbsinit".equals(fullMethodName[0]))
                        return CPLUSPLUS_URL + "cwchar/" + fullMethodName[0] + "/";
                    else
                        return CPLUSPLUS_URL + "cstdlib/" + fullMethodName[0] + "/";
                } else if (isCStdLibFunction(fullMethodName[0]))
                    if (fullMethodName[0].startsWith("wcs"))
                        return CPLUSPLUS_URL + "cwchar/" + fullMethodName[0] + "/";
                    else
                        return CPLUSPLUS_URL + "cstdlib/" + fullMethodName[0] + "/";
                else if (isCStdIOFunction(fullMethodName[0])) {
                    if (fullMethodName[0].matches("v?(f|s)?w(scan|print)f") ||
                            fullMethodName[0].matches("w(c|char|s)$"))
                        return CPLUSPLUS_URL + "cwchar/" + fullMethodName[0] + "/";
                    else
                        return CPLUSPLUS_URL + "cstdio/" + fullMethodName[0] + "/";
                } else if (isCTypeFunction(fullMethodName[0])) {
                    if (fullMethodName[0].startsWith("isw") ||
                            fullMethodName[0].startsWith("tow") ||
                            fullMethodName[0].startsWith("wc"))
                        return CPLUSPLUS_URL + "cwctype/" + fullMethodName[0] + "/";
                    else
                        return CPLUSPLUS_URL + "cctype/" + fullMethodName[0] + "/";
                } else if (isAlgorithmFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "algorithm/" + fullMethodName[0] + "/";
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
        methodName = methodName.replaceAll("_s$", "");
        if (isMathFunction(methodName)) {
            return CPLUSPLUS_URL + "cmath/" + methodName + "/";
        } else if (isCAssert(methodName)) {
            return CPLUSPLUS_URL + "cassert" + methodName + "/";
        } else if (isCMemory(methodName) || isCAlgorithmFunction(methodName)) {
            return CPLUSPLUS_URL + "cstdlib/" + methodName + "/";
        } else if (isCUnicodeCharFunction(methodName)) {
            return CPLUSPLUS_URL + "cuchar/" + methodName + "/";
        } else if (isCStringFunction(methodName)) {
            methodName = methodName.replaceAll("errorlen$", "error");
            if (methodName.startsWith("wmem") || methodName.startsWith("wcs"))
                return CPLUSPLUS_URL + "cwchar/" + methodName + "/";
            else
                return CPLUSPLUS_URL + "cstring/" + methodName + "/";
        } else if (isCMultyStringFunction(methodName)) {
            if (methodName.startsWith("wcr") || methodName.startsWith("wcsr") ||
                    methodName.startsWith("mbr") || methodName.startsWith("mbsr") ||
                    "btowc".equals(methodName) || "mbsinit".equals(methodName))
                return CPLUSPLUS_URL + "cwchar/" + methodName + "/";
            else
                return CPLUSPLUS_URL + "cstdlib/" + methodName + "/";
        } else if (isCStdLibFunction(methodName)) {
            if (methodName.startsWith("wcs"))
                return CPLUSPLUS_URL + "cwchar/" + methodName + "/";
            else
                return CPLUSPLUS_URL + "cstdlib/" + methodName + "/";
        } else if (isCStdIOFunction(methodName)) {
            if (methodName.matches("v?(f|s)?w(scan|print)f") ||
                    methodName.matches(".*w(c|char|s)$"))
                return CPLUSPLUS_URL + "cwchar/" + methodName + "/";
            else
                return CPLUSPLUS_URL + "cstdio/" + methodName + "/";
        } else if (isCTypeFunction(methodName)) {
            if (methodName.startsWith("isw") ||
                    methodName.startsWith("tow") ||
                    methodName.startsWith("wc"))
                return CPLUSPLUS_URL + "cwctype/" + methodName + "/";
            else
                return CPLUSPLUS_URL + "cctype/" + methodName + "/";
        } else if (isAlgorithmFunction(methodName)) {
            return CPLUSPLUS_URL + "algorithm/" + methodName + "/";
        } else if (isVectorContainer(structureName)) {
            return CPLUSPLUS_URL + "vector/" + structureName
                    + "/" + methodName + "/";
        } else if (isSetContainer(structureName)) {
            return CPLUSPLUS_URL + "set/" + structureName
                    + "/" + methodName + "/";
        } else if (isUnorderedSetContainer(structureName)) {
            return CPLUSPLUS_URL + "unordered_set/" + structureName
                    + "/" + methodName + "/";
        } else if (isMapContainer(structureName)) {
            return CPLUSPLUS_URL + "map/" + structureName
                    + "/" + methodName + "/";
        } else if (isUnorderedMapContainer(structureName)) {
            return CPLUSPLUS_URL + "unordered_map/" + structureName
                    + "/" + methodName + "/";
        } else if (isQueueContainer(structureName)) {
            return CPLUSPLUS_URL + "queue/" + structureName
                    + "/" + methodName + "/";
        } else if ("string".equals(structureName)) {
            return CPLUSPLUS_URL + "string/" + structureName
                    + "/" + methodName + "/";
        } else {
            return CPLUSPLUS_URL + structureName
                    + "/" + methodName + "/";
        }
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
