package ru.compscicenter.practice.searcher.sitesearcher;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.InputSource;
import ru.compscicenter.practice.searcher.database.CodeExample;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
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
    private Pattern p;

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
        p = Pattern.compile("[\\s\\t\\+\\-\\*\\/\\=\\(]" + getQuery() + "\\s?\\(");

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
                List<CodeExample> exs = processTempNode(tempNode);
                if (exs != null)
                    examples.addAll(processTempNode(tempNode));
            }
        } catch (Exception e) {
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
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(false);
        StringFromHTMLHandler handler = new StringFromHTMLHandler();
        SAXParser saxParser = saxParserFactory.newSAXParser();

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
        List<JsonNode> answers = collectAnswers(items);

        int length;
        if (answers.size() > 4) {
            length = answers.size() / 2;
        } else {
            length = answers.size();
        }

        for (int i = 0; i < length; i++) {
            JsonNode answer = answers.get(i);
            CodeExample codeExample = new CodeExample();
            codeExample.setLanguage(getLanguage());
            codeExample.setFunction(getQuery());
            codeExample.setSource(node.get("link").asText());

            String body = answer.get("body").asText();
            body = "<root>" + body + "</root>";

            saxParser.parse(new InputSource(new StringReader(body)), handler);
            body = handler.getCleanedString();

            String code = extractCode(body);
            if (findMethodInCode(code)) {
                codeExample.setCodeExample(code);
            } else {
                codeExample.setCodeExample("No code example found in this answer!");
            }

            codeExample.setModificationDate(new Date().getTime());

            if (!"No code example found in this answer!".equals(codeExample.getCodeExample())) {
                temp.add(codeExample);

                logger.info("Code example parameters: " +
                        "programming lang=" + codeExample.getLanguage() + " " +
                        ", function=" + codeExample.getFunction() + " " +
                        ", source=" + codeExample.getSource() + " " +
                        ", modificationDate=" + codeExample.getModificationDate());
            }
        }
        if (temp.size() == 0)
            return null;
        return temp;
    }

    private boolean findMethodInCode(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            Matcher matcher = p.matcher(line);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    private String extractCode(String answer) {
        StringBuilder sb = new StringBuilder();
        String[] lines = answer.split("\n");
        for (String line : lines) {
            if (line.endsWith(";") ||
                    line.endsWith("{") ||
                    line.endsWith(")")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
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
