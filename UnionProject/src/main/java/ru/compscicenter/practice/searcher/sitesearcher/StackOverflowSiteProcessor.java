package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.ContentHandler;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by user on 22.11.2016!
 */
public class StackOverflowSiteProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(StackOverflowSiteProcessor.class);

    /**
     * URL to http://api.stackexchange.com/
     */
    private final static String STACKOVERFLOW_URL = "http://api.stackexchange.com/2.2/";

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String generateRequestURL(String query) {
        return STACKOVERFLOW_URL + "search/advanced?" +
                "q=" + query +
                "&title=" + getLanguage() +
                "&order=desc" +
                "&sort=activity" +
                "&accepted=True" +
                "&answers=2" +
                "&site=stackoverflow";
    }

    @Override
    public List<CodeExample> findAndProcessCodeExamples(String result) {
        List<CodeExample> examples = new ArrayList<>();
        try {
            JsonNode node = mapper.readValue(result, JsonNode.class);
            JsonNode items = node.get("items");

            List<JsonNode> tempNodes = new ArrayList<>();
            for (JsonNode item : items) {
                JsonNode tags = item.get("tags");
                if (languageContainsInTags(tags)) {
                    tempNodes.add(item);
                }
            }

            for (JsonNode tempNode : tempNodes) {
                List<CodeExample> exs = null;
                try {
                    exs = processTempNode(tempNode);
                } catch (Exception e) {
                    logger.error("Sorry, something wrong!", e);
                }

                if (exs != null) {
                    examples.addAll(exs);
                }
            }
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
        return examples;
    }

    private boolean languageContainsInTags(JsonNode tags) {
        for (JsonNode tag : tags) {
            String t = tag.asText().replaceAll("\"", "");
            if (getLanguage().toLowerCase().equals(t)) {
                return true;
            }
        }
        return false;
    }

    private List<CodeExample> processTempNode(JsonNode node) throws Exception {
        int questionId = node.get("question_id").asInt();
        String url = STACKOVERFLOW_URL + "questions/" + questionId + "/answers?" +
                "order=desc" +
                "&sort=activity" +
                "&site=stackoverflow" +
                "&filter=!9YdnSMKKT";
        String result = sendGet(url.trim());
        JsonNode answersNode = mapper.readValue(result, JsonNode.class);
        JsonNode items = answersNode.get("items");
        List<JsonNode> answers = collectAnswers(items);

        int length;
        if (answers.size() > 4) {
            length = answers.size() / 2;
        } else {
            length = answers.size();
        }

        List<CodeExamplesWithSource> codeSourceList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JsonNode answer = answers.get(i);

            String source = node.get("link").asText();
            String body = answer.get("body").asText();

            InputStream input = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            new HtmlParser().parse(input, handler, metadata);
            body = handler.toString();

            body = body.replaceAll("\n+", "\n\n");
            body = body.replaceAll("\\s\n", "\n");

            codeSourceList.add(new CodeExamplesWithSource(source, body));
        }

        for (CodeExamplesWithSource codeWithSource : codeSourceList) {
            codeWithSource.codeFragments = extractCode(codeWithSource.body);
        }

        List<CodeExample> temp = new ArrayList<>();
        for (CodeExamplesWithSource codesWithSource : codeSourceList) {
            codesWithSource.codeFragments.stream().filter(this::findMethodInCode).forEach(s -> {
                CodeExample codeExample = new CodeExample();
                codeExample.setLanguage(getLanguage());
                codeExample.setFunction(getQuery());
                codeExample.setSource(codesWithSource.source);
                codeExample.setCodeExample(s);
                codeExample.setModificationDate(new Date().getTime());

                temp.add(codeExample);
                logger.info("Code example parameters: " +
                        "programming lang=" + codeExample.getLanguage() + " " +
                        ", function=" + codeExample.getFunction() + " " +
                        ", source=" + codeExample.getSource() + " " +
                        ", modificationDate=" + codeExample.getModificationDate());
            });
        }

        if (temp.size() == 0)
            return null;
        return temp;
    }

    private List<JsonNode> collectAnswers(JsonNode items) {
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode item : items) {
            result.add(item);
        }
        Collections.sort(result, new Comparator<JsonNode>() {
            @Override
            public int compare(JsonNode o1, JsonNode o2) {
                if (o1.get("score").asInt() > o2.get("score").asInt())
                    return -1;
                else if (o1.get("score").asInt() < o2.get("score").asInt())
                    return 1;
                else
                    return 0;
            }
        });
        return result;
    }

    @Override
    public String getSiteName() {
        return "http://stackoverflow.com/";
    }
}
