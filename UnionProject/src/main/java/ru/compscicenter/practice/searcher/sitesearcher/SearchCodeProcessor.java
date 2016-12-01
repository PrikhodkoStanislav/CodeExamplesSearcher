package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public List<CodeExample> findAndProcessCodeExamples(String result) {
        List<CodeExample> examples = new ArrayList<>();
        List<CodeExamplesWithSource> codeSourceList = new ArrayList<>();
        //Pattern p = Pattern.compile("<td class=\"code\">(.*)</td>");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            JsonNode results = node.get("results");
            for (JsonNode res : results) {
                String url = res.get("url").asText();
                String exampleFormJSON = sendGet(url);

                InputStream input = new ByteArrayInputStream(exampleFormJSON.getBytes(StandardCharsets.UTF_8));
                ContentHandler handler = new BodyContentHandler();
                Metadata metadata = new Metadata();
                new HtmlParser().parse(input, handler, metadata);
                exampleFormJSON = handler.toString();

                exampleFormJSON = exampleFormJSON.replaceAll("\n+", "\n\n");
                exampleFormJSON = exampleFormJSON.replaceAll("\\s*\n", "\n");

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

            for (CodeExamplesWithSource codeWithSource : codeSourceList) {
                codeWithSource.codeFragments = extractCode(codeWithSource.body);
            }

            for (CodeExamplesWithSource codesWithSource : codeSourceList) {
                codesWithSource.codeFragments.stream().filter(this::findMethodInCode).forEach(s -> {
                    CodeExample codeExample = new CodeExample();
                    codeExample.setLanguage(getLanguage());
                    codeExample.setFunction(getQuery());
                    codeExample.setSource(codesWithSource.source);
                    codeExample.setCodeExample(s);
                    codeExample.setModificationDate(new Date().getTime());

                    examples.add(codeExample);
                    logger.info("Code example parameters: " +
                            "programming lang=" + codeExample.getLanguage() + " " +
                            ", function=" + codeExample.getFunction() + " " +
                            ", source=" + codeExample.getSource() + " " +
                            ", modificationDate=" + codeExample.getModificationDate());
                });
            }
        } catch (IOException | SAXException | TikaException e) {
            logger.error("Sorry, something wrong!", e);
        }
        if (examples.size() == 0)
            return null;
        return examples;
    }

    @Override
    public String getSiteName() {
        return SEARCHCODE_URL.substring(0, SEARCHCODE_URL.length() - 17);
    }
}
