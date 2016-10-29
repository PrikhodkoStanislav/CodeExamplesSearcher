package ru.compscicenter.practice.searcher;

import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.List;

/**
 * Created by user on 06.10.2016!
 */
public class ProjectCodeFormatter {
    private CodeFormatter codeFormatter;

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
        sb.append("<h3 align=\"center\">Code examples from sites</h3>");
        sb.append("<body>");
        for (CodeExample example : examples) {
            sb.append("<pre>").append(example.toString("html")).append("</pre><br>");
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public String createTxt(List<CodeExample> examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================================").append(System.getProperty("line.separator"));
        sb.append("||                       Code examples from sites:                          ||").append(System.getProperty("line.separator"));
        sb.append("==============================================================================").append(System.getProperty("line.separator"));
        for (CodeExample example : examples) {
            sb.append("==============================================================================").append(System.getProperty("line.separator"));
            String code = example.getCodeExample();
            code = code.replaceAll("&lt;", "<");
            code = code.replaceAll("&gt;", ">");
            example.setCodeExample(code);
            sb.append(example.toString("txt"));
            sb.append("==============================================================================").append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void beautifyCode(List<CodeExample> l1) {
        for (CodeExample codeExample : l1) {
            codeExample.setCodeExample(toPrettyCode(codeExample.getCodeExample()));
        }
    }

    public String toPrettyCode(String code) {
        code = code.replaceAll("\\*/", "\\*\\/" + System.getProperty("line.separator"));
        code = code.replaceAll("#", System.getProperty("line.separator") + "#");

        int intMain = code.indexOf("int main");
        code =  intMain > 0 ? (code.substring(0, intMain) +
                System.getProperty("line.separator") + code.substring(intMain)) : code;

        TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code,
                0, code.length(), 0, System.getProperty("line.separator"));

        IDocument document = new Document(code);
        try {
            edit.apply(document);
        } catch (MalformedTreeException | BadLocationException e) {
            e.printStackTrace();
        }
        return document.get();
    }
}
