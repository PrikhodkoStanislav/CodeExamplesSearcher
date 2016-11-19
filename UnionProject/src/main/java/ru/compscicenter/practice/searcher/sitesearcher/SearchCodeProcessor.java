package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 19.11.2016!
 */
public class SearchCodeProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(CPPReferenceSiteProcessor.class);

    /**
     * URL to http://searchcode.com/api/
     */
    private final static String SEARCHCODE_URL = "http://searchcode.com/api/codesearch_I/";

    private Map<String, Integer> languages;

    public SearchCodeProcessor() {
        languages = new HashMap<>();
        languages.put("C", 28);
        languages.put("C++", 16);
        languages.put("Java", 23);
        languages.put("Python", 19);
    }

    @Override
    public String generateRequestURL(String query) {
        return SEARCHCODE_URL + "?q=" + query + "&lan=" + languages.get(getLanguage());
    }

    @Override
    public List<CodeExample> findAndProcessCodeExamples(String result) {
        List<CodeExample> examples = new ArrayList<>();
        Pattern p = Pattern.compile("<td class=\"code\">(.*)</td>");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            JsonNode results = node.get("results");
            for (JsonNode res : results) {
                String url = res.get("url").asText();
                String exampleFormJSON = sendGet(url);

                Matcher matcher = p.matcher(exampleFormJSON);
                String codeExample;
                while (matcher.find()) {
                    codeExample = matcher.group(1);
                    codeExample = codeExample
                            .replaceAll(
                                    "<(/)?(span|a)(\\s((class=\"[a-z]+\")|(name=\"l-\\d+\")))?>"
                                    , ""
                            );
                    codeExample = codeExample.replaceAll("\\s+", " ");

                    codeExample = codeExample.replaceAll("\\*/", "\\*\\/\n");
                    codeExample = codeExample.replaceAll("#", "\n#");

                    /*int intMain = codeExample.indexOf("int main");
                    codeExample =  intMain > 0 ? (codeExample.substring(0, intMain) +
                            "\n" + codeExample.substring(intMain)) : codeExample;*/

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
                }
            }
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
        return examples;
    }

    @Override
    public String getSiteName() {
        return SEARCHCODE_URL.substring(0, SEARCHCODE_URL.length() - 17);
    }
}
