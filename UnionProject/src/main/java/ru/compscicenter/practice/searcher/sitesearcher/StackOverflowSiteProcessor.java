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
 * Created by user on 22.11.2016!
 */
public class StackOverflowSiteProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(StackOverflowSiteProcessor.class);

    /**
     * URL to http://api.stackexchange.com/
     */
    private final static String STACKOVERFLOW_URL = "http://api.stackexchange.com/2.2/";

    private ObjectMapper mapper = new ObjectMapper();
    private Pattern p = Pattern.compile("[\\s\\+\\-\\*\\/\\=\\(]" + getQuery() + "\\s?\\(");

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
                examples.addAll(processTempNode(tempNode));
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

    private List<CodeExample> processTempNode(JsonNode node) throws IOException {
        List<CodeExample> temp = new ArrayList<>();
        int questionId = node.get("question_id").asInt();
        String url = STACKOVERFLOW_URL + "questions/" + questionId + "/answers?" +
                "order=desc" +
                "&sort=activity" +
                "&site=stackoverflow" +
                "&filter=!9YdnSMKKT";
        String result = sendGet(url.trim());
        JsonNode answersNode = mapper.readValue(result, JsonNode.class);
        JsonNode items = answersNode.get("items");

        Matcher matcher;

        for (JsonNode item : items) {
            CodeExample codeExample = new CodeExample();
            codeExample.setLanguage(getLanguage());
            codeExample.setFunction(getQuery());
            codeExample.setSource(node.get("link").asText());

            String answer = item.get("body").asText();
            matcher = p.matcher(answer);
            if (matcher.find()) {
                answer = matcher.group();
                codeExample.setCodeExample(answer);
            } else {
                codeExample.setCodeExample("No code example found in this answer!");
            }

            codeExample.setModificationDate(new Date().getTime());

            logger.info("Code example parameters: " +
                    "programming lang=" + codeExample.getLanguage() + " " +
                    ", function=" + codeExample.getFunction() + " " +
                    ", source=" + codeExample.getSource() + " " +
                    ", modificationDate=" + codeExample.getModificationDate());

            if (!"No code example found in this answer!".equals(codeExample.getCodeExample()))
            temp.add(codeExample);
        }
        return temp;
    }

    @Override
    public String getSiteName() {
        return "http://stackoverflow.com/";
    }
}
