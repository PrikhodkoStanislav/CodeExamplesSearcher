package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public List<CodeExamplesWithSource> findAndProcessCodeExamples(String result) {
        List<CodeExamplesWithSource> exs = null;
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

            exs = new ArrayList<>();
            for (JsonNode tempNode : tempNodes) {
                try {
                    exs.addAll(processTempNode(tempNode));
                } catch (Exception e) {
                    logger.error("Sorry, something wrong!", e);
                }
            }
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
        return exs;
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

    private List<CodeExamplesWithSource> processTempNode(JsonNode node) throws Exception {
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

            body = cleanTextFromHTMlTags(body);

            body = body.replaceAll("\n+", "\n\n");
            body = body.replaceAll("\\s+\n", "\n");

            codeSourceList.add(new CodeExamplesWithSource(source, body));
        }
        return codeSourceList;
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

}
