package ru.compscicenter.practice.searcher;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import ru.compscicenter.practice.searcher.codeformatter.AStyleFormatter;
import ru.compscicenter.practice.searcher.codeformatter.HandwrittenCodeFormatter;
import ru.compscicenter.practice.searcher.database.CodeExample;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by user on 06.10.2016!
 */
public class ProjectCodeFormatter {
    private final static Logger logger = Logger.getLogger(ProjectCodeFormatter.class);

    private Preferences prefs = Preferences.userRoot().node("settings");
    private final static int defaultFormatter = 2;

    private String reportDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

    /**
     * Create result HTML or TXT file
     * @param functionName function name
     * @param examples list with search results
     * @param format file extension
     * @return string for the result file
     * */
    public String createResultFile(String functionName, List<CodeExample> examples,
                                   String format, List<CodeExample> codeFromSublime, String string) {
        if ("html".equals(format))
            return createHtml(functionName, examples, codeFromSublime, string);
        else
            return createTxt(functionName, examples);
    }

    /**
     * Create string for HTML file
     * @param functionName function name
     * @param examples list with search results
     * @return html-string
     * */
    public String createHtml(String functionName, List<CodeExample> examples,
                             List<CodeExample> codeFromSublime, String string) {
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
                .append("\"").append(functionName).append("\"")
                .append("</span>")
                .append("</h3>");
        sb.append("<p><b>Last report date:</b> ").append(reportDate).append("</p>");
        sb.append("<a href=\"http://localhost:8080/settings\">Settings for searcher</a>");
        sb.append("<p><b>String:</b> ");
        sb.append(string);
        sb.append("</p>");
        sb.append("<body>");
        sb.append("<table>");
        sb.append("<tr>")
                .append("<th>").append("YOUR SOURCE").append("</th>")
                .append("<th>").append("CODE EXAMPLE FROM YOUR ACTIVE FILE").append("</th>")
                .append("</tr>");
        if (codeFromSublime != null) {
            for (CodeExample code : codeFromSublime) {
                String source = code.getSource();
                int end = source.lastIndexOf(".c :");
                source = source.replaceAll("\\\\", "/");
                sb.append("<tr>")
                    .append("<td>")
                        .append("<a href=\"" + "file:///")
                        .append(source.substring(0, end + 2)).append("\">")
                        .append(source)
                        .append("</a>")
                    .append("</td>")
                    .append("<td><pre>").append(code.getCodeExample()).append("</pre></td>")
                    .append("</tr>");
            }
        }
        sb.append("</table>");
        sb.append("<table>");
        sb.append("<tr>")
                .append("<th>").append("SOURCE").append("</th>")
                .append("<th>").append("CODE EXAMPLE").append("</th>")
                .append("</tr>");
        for (CodeExample example : examples) {
            String source = example.getSource();
            int end = source.lastIndexOf(".c :");
            source = source.replaceAll("\\\\", "/");
            sb.append("<tr>")
                    .append("<td>")
                        .append("<a href=\"")
                            .append(source.startsWith("http") ? source :
                                    "file:///" +
                                            source.substring(0, end + 2)).append("\">")
                            .append(source)
                        .append("</a>")
                    .append("</td>")
                    .append("<td><pre>").append(example.getCodeExample()).append("</pre></td>")
                    .append("</tr>");
        }
        sb.append("</table>")
            .append("</body>")
            .append("</html>");
        return sb.toString();
    }

    /**
     * Create string for TXT file
     * @param functionName function name
     * @param examples list with search results
     * @return txt-string
     * */
    public String createTxt(String functionName, List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================================\n");
        sb.append("||                       Code examples for function \"").append(functionName)
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
            sb.append("==============================================================================\n");
        }
        return sb.toString();
    }

    /**
     * Format code examples
     * @param examples list with search results
     * */
    public void beautifyCode(List<CodeExample> examples) {
        for (CodeExample codeExample : examples) {
            codeExample.setCodeExample(lineSeparatorUnify(codeExample.getCodeExample()));
            codeExample.setCodeExample(toPrettyCode(codeExample.getCodeExample()));
        }
    }

    /**
     * Unifies line separator
     * @param code code for modifications
     * @return string with normalized line separator
     * */
    public String lineSeparatorUnify(String code) {
        String result = code;
        result = result.replaceAll("\\\\r\\\\n", "\n");
        result = result.replaceAll("#.*\n", "");
        return result;
    }

    /**
     * Formats code to C-style
     * @param code code for formatting
     * @return formatted string
     * */
    public String toPrettyCode(String code) {
        logger.setLevel(Level.ERROR);

//        System.out.println(code);

        // For speed!
//        String result = HandwrittenCodeFormatter.format(code);
//
//        System.out.println(result);

        int formatter = prefs.getInt("formatter", defaultFormatter);

        if (formatter == 1) {
            String fileName = "example.c";
            write(fileName, code);

            System.out.println(code);
            AStyleFormatter.main(null);


            String result = code;
            try {
                result = read(fileName);
            } catch (FileNotFoundException e) {
                logger.error("File is not exist!", e);
            }

            System.out.println(result);

            return result;

        } else if (formatter == 2) {
            IDocument document = new Document(code);
            try {
                CodeFormatter codeFormatter = ToolFactory.createDefaultCodeFormatter(null);
                TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, "\n");
                edit.apply(document);
            } catch (Exception e) {
                logger.error("Sorry, something wrong!", e);
            }
            return document.get();
        } else if (formatter == 3) {
            return code;
        }

        return code;
    }

    public void write(String fileName, String text) {
        File file = new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read(String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
