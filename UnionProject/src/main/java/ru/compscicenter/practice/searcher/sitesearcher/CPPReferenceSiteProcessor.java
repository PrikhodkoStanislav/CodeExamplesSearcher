package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodionova Darya on 28.09.2016!
 */
public class CPPReferenceSiteProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(CPPReferenceSiteProcessor.class);

    /**
     * URL to http://en.cppreference.com/w/
     */
    private final static String CPPREFERENCE_URL = "http://en.cppreference.com/w/";

    @Override
    public String generateRequestURL(final String query) {
        String[] fullMethodName = query.split("::");
        if (fullMethodName.length == 1) {
            fullMethodName = query.split(" ");
            if (fullMethodName.length == 1) {
                fullMethodName[0] = fullMethodName[0].replaceAll("_s$", "");
                if ("sizeof".equals(fullMethodName[0]))
                    return CPPREFERENCE_URL + "cpp/language/" + fullMethodName[0];
                else if (isMathFunction(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/numeric/math/" + fullMethodName[0];
                else if (isCAlgorithmFunction(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/algorithm/" + fullMethodName[0];
                else if (isCAssert(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/error/" + fullMethodName[0];
                else if (isCMemory(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/memory/" + fullMethodName[0];
                else if (isCEnvironment(fullMethodName[0]) || isCSignal(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/program/" + fullMethodName[0];
                else if (isCTime(fullMethodName[0]))
                    return CPPREFERENCE_URL + "c/chrono/" + fullMethodName[0];
                else if (isCFenv(fullMethodName[0])) {
                    if (fullMethodName[0].matches("fe((g|s)et)(env|exceptflag|round)"))
                        return CPPREFERENCE_URL + "c/numeric/fenv/" +
                                "fe"+ fullMethodName[0].substring(5, fullMethodName[0].length());
                    return CPPREFERENCE_URL + "c/numeric/fenv/" + fullMethodName[0];
                } else if (isCStringFunction(fullMethodName[0]) ||
                        isCStdLibFunction(fullMethodName[0]) ||
                        isCTypeFunction(fullMethodName[0])) {
                    fullMethodName[0] = fullMethodName[0].replaceAll("errorlen$", "error");
                    return getStdLibUrl(fullMethodName[0]);
                } else if (isCMultyStringFunction(fullMethodName[0]) || isCUnicodeCharFunction(fullMethodName[0])) {
                    return CPPREFERENCE_URL + "c/string/multibyte/" + fullMethodName[0];
                } else if (isCStdIOFunction(fullMethodName[0])) {
                    return getStdIOUrl(fullMethodName[0]);
                } else if (isAlgorithmFunction(fullMethodName[0]))
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
        methodName = methodName.replaceAll("_s$", "");
        if ("sizeof".equals(methodName)) {
            return CPPREFERENCE_URL + "cpp/language/" + methodName;
        } else if (isMathFunction(methodName)) {
            return CPPREFERENCE_URL + "c/numeric/math/" + methodName;
        } else if (isCAssert(methodName)) {
            return CPPREFERENCE_URL + "c/error/" + methodName;
        } else if (isCAlgorithmFunction(methodName)) {
            return CPPREFERENCE_URL + "c/algorithm/" + methodName;
        } else if (isCMemory(methodName)) {
            return CPPREFERENCE_URL + "c/memory/" + methodName;
        } else if (isCEnvironment(methodName) || isCSignal(methodName)) {
            return CPPREFERENCE_URL + "c/program/" + methodName;
        } else if (isCTime(methodName)) {
            return CPPREFERENCE_URL + "c/chrono/" + methodName;
        } else if (isCFenv(methodName)) {
            if (methodName.matches("fe((g|s)et)(env|exceptflag|round)"))
                return CPPREFERENCE_URL + "c/numeric/fenv/" +
                        "fe"+ methodName.substring(5, methodName.length());
            return CPPREFERENCE_URL + "c/numeric/fenv/" + methodName;
        } else if (isCStringFunction(methodName) ||
                isCStdLibFunction(methodName) ||
                isCTypeFunction(methodName)) {
            methodName = methodName.replaceAll("errorlen$", "error");
            return getStdLibUrl(methodName);
        } else if (isCMultyStringFunction(methodName) || isCUnicodeCharFunction(methodName)) {
            return CPPREFERENCE_URL + "c/string/multibyte/" + methodName;
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
        if (methodName.matches("s?rand"))
            return CPPREFERENCE_URL + "c/numeric/random/rand";
        else if (methodName.matches("ato(ll?|i)?"))
            return CPPREFERENCE_URL + "c/string/byte/atoi";
        else if (methodName.matches("atof"))
            return CPPREFERENCE_URL + "c/string/byte/atof";
        else if (methodName.matches("(str|wcs)to(ll?)"))
            return CPPREFERENCE_URL + "c/string/" + byteOrWideMethod(methodName) + "tol";
        else if (methodName.matches("(str|wcs)toull?"))
            return CPPREFERENCE_URL + "c/string/" + byteOrWideMethod(methodName) + "toul";
        else if (methodName.matches("(str|wcs)to(f|l?d)"))
            return CPPREFERENCE_URL + "c/string/" + byteOrWideMethod(methodName) + "tof";
        else if (methodName.matches("(str|wcs)to(i|u)max"))
            return CPPREFERENCE_URL + "c/string/" + byteOrWideMethod(methodName) + "toimax";
        else if (methodName.startsWith("w") || methodName.startsWith("isw") ||
                methodName.startsWith("tow") || methodName.startsWith("wc"))
            return CPPREFERENCE_URL + "c/string/wide/" + methodName;
        else
            return CPPREFERENCE_URL + "c/string/byte/" + methodName;
    }

    private String byteOrWideMethod(String methodName) {
        if (methodName.startsWith("str"))
            return "byte/str";
        else
            return "wide/wcs";
    }

    private String getStdIOUrl(String methodName) {
        if (methodName.matches("(f|s)?scanf"))
            return CPPREFERENCE_URL + "c/io/fscanf";
        else if (methodName.matches("v(f|s)?scanf"))
            return CPPREFERENCE_URL + "c/io/vfscanf";
        else if (methodName.matches("(f|sn?)?printf"))
            return CPPREFERENCE_URL + "c/io/fprintf";
        else if (methodName.matches("v(f|sn?)?printf"))
            return CPPREFERENCE_URL + "c/io/vfprintf";
        else if (methodName.matches("(f|s)?wscanf"))
            return CPPREFERENCE_URL + "c/io/fwscanf";
        else if (methodName.matches("v(f|s)?wscanf"))
            return CPPREFERENCE_URL + "c/io/vfwscanf";
        else if (methodName.matches("(f|s)?wprintf"))
            return CPPREFERENCE_URL + "c/io/fwprintf";
        else if (methodName.matches("v(f|s)?wprintf"))
            return CPPREFERENCE_URL + "c/io/vfwprintf";
        else if (methodName.matches("f?putc"))
            return CPPREFERENCE_URL + "c/io/fputc";
        else if (methodName.matches("f?getc"))
            return CPPREFERENCE_URL + "c/io/fgetc";
        else if (methodName.matches("f?putwc"))
            return CPPREFERENCE_URL + "c/io/fputwc";
        else if (methodName.matches("f?getwc"))
            return CPPREFERENCE_URL + "c/io/fgetwc";
        else
            return CPPREFERENCE_URL + "c/io/" + methodName;
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

    @Override
    public List<CodeExamplesWithSource> findAndProcessCodeExamples(final String result) {
        logger.setLevel(Level.INFO);

        List<CodeExamplesWithSource> codeSourceList = new ArrayList<>();

        String inp = result;
        /*Pattern patternForCodeBlock = Pattern.compile("<div class=\"t-example\">.*<div class=\"c(pp)? source-c(pp)?\"><pre class=\"de1\">(.*)</pre></div></div><p>");
        Pattern patternForCodeCleaning = Pattern.compile("<(/)?(span|a)(\\s((class=\"[a-z]{2}\\d+\")|(href=\"https?://[a-zA-Z\\.]([_a-zA-Z\\./])*\")))?>");
        Matcher matcher = patternForCodeBlock.matcher(result);
        String codeExample;
        while (matcher.find()) {
            codeExample = matcher.group(3);
            codeExample = codeExample.replaceAll(patternForCodeCleaning.pattern(), "");

            codeExample = codeExample.replaceAll("&#40;", "(");
            codeExample = codeExample.replaceAll("&#41;", ")");
            codeExample = codeExample.replaceAll("&#91;", "[");
            codeExample = codeExample.replaceAll("&#93;", "]");
            codeExample = codeExample.replaceAll("&#123;", "{");
            codeExample = codeExample.replaceAll("&#125;", "}");
            codeExample = codeExample.replaceAll("&#160;", " ");
            codeExample = codeExample.replaceAll("&quot;", "\"");
            codeExample = codeExample.replaceAll("&amp;", "&");
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

            inp = inp.replaceAll("\\s?\\(((until|since) C[0-9]{2})\\)\\s?", "");

            inp = inp.replaceAll("&#40;", "(");
            inp = inp.replaceAll("&#41;", ")");
            inp = inp.replaceAll("&#91;", "[");
            inp = inp.replaceAll("&#93;", "]");
            inp = inp.replaceAll("&#123;", "{");
            inp = inp.replaceAll("&#125;", "}");
            inp = inp.replaceAll("&quot;", "\"");
            inp = inp.replaceAll("&amp;", "&");

            String url = generateRequestURL(getQuery());
            codeSourceList.add(new CodeExamplesWithSource(url, inp));
        } catch (IOException | SAXException | TikaException e) {
            logger.error("Sorry, something wrong", e);
        }
        if (codeSourceList.size() == 0)
            return null;
        return codeSourceList;
    }

}
