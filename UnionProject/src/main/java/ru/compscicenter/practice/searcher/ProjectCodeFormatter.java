package ru.compscicenter.practice.searcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 06.10.2016!
 */
public class ProjectCodeFormatter {
    private final static Logger logger = Logger.getLogger(ProjectCodeFormatter.class);

    private CodeFormatter codeFormatter;
    private String reportDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

    public ProjectCodeFormatter() {
        codeFormatter = ToolFactory.createDefaultCodeFormatter(null);
    }

    public String createResultFile(List<CodeExample> examples, String format) {
        if ("html".equals(format))
            return createHtml(examples);
        else
            return createTxt(examples);
    }

    public String createHtml(List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>\n" +
                "body {\n" +
                "    background-color: #E4F4DE;\n" +
                "}\n" +
                "\n" +
                "h3 {\n" +
                "    color: #26557B;\n" +
                "    font-family: verdana;\n" +
                "    font-size: 200%;" +
                "}\n" +
                "\n" +
                "span {\n" +
                "    font-family: courier;\n" +
                "    color: #424582;\n" +
                "}\n" +
                "\n" +
                "table, td, th {\n" +
                "    border: 1px solid black;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "    background-color: #FFFFFF;\n" +
                "    border-collapse: collapse;\n" +
                "    width: 100%;\n" +
                "}\n" +
                "\n" +
                "td {\n" +
                "    text-indent: 0px;\n" +
                "}\n" +
                "\n" +
                "th {\n" +
                "    text-align: left;\n" +
                "    background-color: #6AB75D;\n" +
                "    height: 30px;\n" +
                "}\n" +
                "</style>");
        sb.append("</head>");
        sb.append("<h3>Code examples for function ")
                .append("<span>")
                .append("\"").append(examples.get(0).getFunction()).append("\"")
                .append("</span>")
                .append("</h3>");
        sb.append("<p><b>Last report date:</b> ").append(reportDate).append("</p>");
        sb.append("<body>");
        sb.append("<table>");
        sb.append("<tr>")
            .append("<th>").append("SOURCE").append("</th>")
            .append("<th>").append("CODE EXAMPLE").append("</th>")
            .append("</tr>");
        for (CodeExample example : examples) {
            sb.append("<tr>")
                .append("<td>").append(example.getSource()).append("</td>")
                .append("<td><pre>").append(example.getCodeExample()).append("</pre></td>")
                .append("</tr>");
        }
        sb.append("</table>")
            .append("</body>")
            .append("</html>");
        return sb.toString();
    }

    public String createTxt(List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================================\n");
        sb.append("||                       Code examples for function \"").append(examples.get(0).getFunction())
                .append("\"                     ||\n");
        sb.append("==============================================================================\n");
        sb.append("Last report date: ").append(reportDate).append("\n");
        sb.append("==============================================================================\n");
        for (CodeExample example : examples) {
            sb.append("==============================================================================\n");
            String code = example.getCodeExample();
            code = code.replaceAll("&lt;", "<");
            code = code.replaceAll("&gt;", ">");
            example.setCodeExample(code);
            sb.append(example.getSource()).append("\n").append(example.getCodeExample());
            sb.append(example.toString("txt"));
            sb.append("==============================================================================\n");
        }
        return sb.toString();
    }

    public void beautifyCode(List<CodeExample> l1) {
        for (CodeExample codeExample : l1) {
            codeExample.setCodeExample(lineSeparatorUnify(codeExample.getCodeExample()));
            codeExample.setCodeExample(toPrettyCode(codeExample.getCodeExample()));
        }
    }

    public String lineSeparatorUnify(String code) {
        String result = code;
        result = result.replaceAll("\\\\r\\\\n", "\n");
        result = result.replaceAll("#.*\n", "");
        return result;
    }

    public String toPrettyCode(String code) {
        logger.setLevel(Level.ERROR);

        TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, "\n");

        IDocument document = new Document(code);
        try {
            edit.apply(document);
        } catch (MalformedTreeException | BadLocationException e) {
            logger.error("Sorry, something wrong!", e);
        }
        return document.get();
    }
}
