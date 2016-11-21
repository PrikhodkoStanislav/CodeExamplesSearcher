package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by user on 22.11.2016!
 */
public class StackOverflowSiteProcessor extends SiteProcessor {
    private final static Logger logger = Logger.getLogger(StackOverflowSiteProcessor.class);

    /**
     * URL to http://api.stackexchange.com/
     */
    private final static String STACKOVERFLOW_URL = "http://api.stackexchange.com/2.2/search/advanced?";

    @Override
    public String generateRequestURL(String query) {
        return STACKOVERFLOW_URL + "q=" + query +
                "&title=" + getLanguage() +
                "order=desc" +
                "&sort=activity" +
                "&accepted=True" +
                "&answers=5" +
                "&site=stackoverflow";
    }

    @Override
    public List<CodeExample> findAndProcessCodeExamples(String result) {
        List<CodeExample> examples = new ArrayList<>();
        Pattern p = Pattern.compile("<code>(.*)</code>");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(result, JsonNode.class);
            JsonNode items = node.get("items");

            List<JsonNode> tempNodes = new ArrayList<>();
            for (JsonNode res : items) {
                JsonNode tags = res.get("tags");
                for (JsonNode tag : tags) {
                    if (getLanguage().equals(tag.asText())) {
                        tempNodes.add(tag);
                    }
                }
            }

            examples.addAll(tempNodes.stream().map(this::processTempNode).collect(Collectors.toList()));
        } catch (IOException e) {
            logger.error("Sorry, something wrong!", e);
        }
        return examples;
    }

    private CodeExample processTempNode(JsonNode tempNode) {
        return null;
    }

    @Override
    public String getSiteName() {
        return "http://stackoverflow.com/";
    }
}
