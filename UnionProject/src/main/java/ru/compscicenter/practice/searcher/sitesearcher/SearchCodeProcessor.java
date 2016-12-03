package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 19.11.2016!
 */
public class SearchCodeProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(SearchCodeProcessor.class);

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
    public List<CodeExamplesWithSource> findAndProcessCodeExamples(String result) {
        List<CodeExamplesWithSource> codeSourceList = new ArrayList<>();
        //Pattern p = Pattern.compile("<td class=\"code\">(.*)</td>");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            JsonNode results = node.get("results");
            for (JsonNode res : results) {
                String url = res.get("url").asText();
                String exampleFormJSON = sendGet(url);

                exampleFormJSON = cleanTextFromHTMlTags(exampleFormJSON);
                exampleFormJSON = exampleFormJSON.replaceAll("\n+", "\n\n");
                exampleFormJSON = exampleFormJSON.replaceAll("\\s+\n", "\n");

                if (url.contains("/87593000"))
                    codeSourceList.add(new CodeExamplesWithSource(url, exampleFormJSON));

                /*Matcher matcher = p.matcher(exampleFormJSON);
                String codeExample;
                while (matcher.find()) {
                    codeExample = matcher.group(1);
                    codeExample = codeExample
                            .replaceAll(
                                    "<(/)?(a|div|span)(\\s((class=\"[a-z]+\")|(name=\"l-\\d+\")))?>"
                                    , ""
                            );
                    codeExample = codeExample.replaceAll("</?pre>", "");
                    codeExample = codeExample.replaceAll("\\s+", " ");

                    codeExample = codeExample.replaceAll("\\*//*", "\\*\\/\n");
                    codeExample = codeExample.replaceAll("#", "\n#");

                    int intMain = codeExample.indexOf("int main");
                    codeExample =  intMain > 0 ? (codeExample.substring(0, intMain) +
                            "\n" + codeExample.substring(intMain)) : codeExample;

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
            }
        } catch (IOException | SAXException | TikaException e) {
            logger.error("Sorry, something wrong!", e);
        }
        if (codeSourceList.size() == 0)
            return null;
        return codeSourceList;
    }

}
