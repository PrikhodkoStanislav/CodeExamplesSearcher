package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodionova Darya on 28.09.2016!
 */
public class CPlusPlusSiteProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(CPlusPlusSiteProcessor.class);

    /**
     * URL to http://www.cplusplus.com/reference/
     */
    private final static String CPLUSPLUS_URL = "http://www.cplusplus.com/reference/";

    public String generateRequestURL(final String query) {
        String[] fullMethodName = query.split("::");
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                fullMethodName[0] = fullMethodName[0].replaceAll("_s$", "");
                if (isMathFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cmath/" + fullMethodName[0] + "/";
                else if (isCAssert(fullMethodName[0])) {
                    if (fullMethodName[0].matches("errno"))
                        return CPLUSPLUS_URL + "cerrno/" + fullMethodName[0] + "/";
                    return CPLUSPLUS_URL + "cassert/" + fullMethodName[0] + "/";
                } else if (isCFenv(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cfenv/" + fullMethodName[0] + "/";
                else if (isCTime(fullMethodName[0]))
                    return CPLUSPLUS_URL + "ctime/" + fullMethodName[0] + "/";
                else if (isCEnvironment(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cstdlib/" + fullMethodName[0] + "/";
                else if (isCMemory(fullMethodName[0]) || isCAlgorithmFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cstdlib/" + fullMethodName[0] + "/";
                else if (isCUnicodeCharFunction(fullMethodName[0]))
                    return CPLUSPLUS_URL + "cuchar/" + fullMethodName[0] + "/";
                else if (isCSignal(fullMethodName[0]))
                    return CPLUSPLUS_URL + "csignal/" + fullMethodName[0] + "/";
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
            if (methodName.matches("errno"))
                return CPLUSPLUS_URL + "cerrno/" + methodName + "/";
            return CPLUSPLUS_URL + "cassert" + methodName + "/";
        } else if (isCEnvironment(methodName))
            return CPLUSPLUS_URL + "cstdlib/" + methodName + "/";
        else if (isCMemory(methodName) || isCAlgorithmFunction(methodName))
            return CPLUSPLUS_URL + "cstdlib/" + methodName + "/";
        else if (isCFenv(methodName))
            return CPLUSPLUS_URL + "cfenv/" + methodName + "/";
        else if (isCTime(methodName))
            return CPLUSPLUS_URL + "ctime/" + methodName + "/";
        else if (isCUnicodeCharFunction(methodName))
            return CPLUSPLUS_URL + "cuchar/" + methodName + "/";
        else if (isCSignal(methodName))
            return CPLUSPLUS_URL + "csignal/" + methodName + "/";
        else if (isCStringFunction(methodName)) {
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

    @Override
    public List<CodeExamplesWithSource> findAndProcessCodeExamples(final String result) {
        logger.setLevel(Level.INFO);

        List<CodeExamplesWithSource> codeSourceList = new ArrayList<>();

        String inp = result;
        /*Pattern patternForCodeBlock = Pattern.compile("Example.*<code>((<cite>.*)?(<dfn>.*)?<var>.*})</code>");
        Pattern patternForCodeCleaning = Pattern.compile("<(/)?(cite|dfn|var|span|kbd)>");
        Matcher matcher = patternForCodeBlock.matcher(result);
        String codeExample;
        while (matcher.find()) {
            codeExample = matcher.group(1);
            codeExample = codeExample.replaceAll(patternForCodeCleaning.pattern(), "");
            codeExample = codeExample.replaceAll("\\s+", " ");

            codeExample = codeExample.replaceAll("\\*//*", "\\*\\/\n");
            codeExample = codeExample.replaceAll("#", "\n#");

            int intMain = codeExample.indexOf("int main");
            codeExample =  intMain > 0 ? (codeExample.substring(0, intMain) +
                    "\n" + codeExample.substring(intMain)) : codeExample;

            String url = generateRequestURL(getQuery());
            CodeExample ce = new CodeExample();
            ce.setLanguage(getLanguage());
            ce.setSource(url);
            ce.setFunction(getQuery());
            ce.setCodeExample(codeExample);
            ce.setModificationDate(new Date().getTime());
            logger.info("Code example parameters: " +
                    "programming lang=" + ce.getLanguage() + " " +
                    ", function=" + ce.getFunction() + " " +
                    ", source=" + ce.getSource() + " " +
                    ", modificationDate=" + ce.getModificationDate());
            examples.add(ce);
        }*/

        try {
            inp = cleanTextFromHTMlTags(inp);

            inp = inp.replaceAll("\n+", "\n\n");
            inp = inp.replaceAll("\\s\n", "\n");

            String url = generateRequestURL(getQuery());
            codeSourceList.add(new CodeExamplesWithSource(url, inp));
        } catch (Exception e) {
            logger.error("Sorry, something wrong", e);
        }
        if (codeSourceList.size() == 0)
            return null;
        return codeSourceList;
    }

}
