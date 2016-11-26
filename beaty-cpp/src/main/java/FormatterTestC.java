import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import java.io.*;
import java.util.*;

public class FormatterTestC {

    private CodeFormatter codeFormatter;

    public FormatterTestC()
    {
        codeFormatter = org.eclipse.cdt.core.ToolFactory.createDefaultCodeFormatter(null);
    }

    public String format(String source) {
        // int kind, String source, int offset, int length, int indentationLevel, String lineSeparator

        TextEdit edit = codeFormatter.format(0, source, 0, source.length(), 0, null);

        IDocument document= new Document(source);
        try {
            edit.apply(document);
        } catch (MalformedTreeException e) {
            ;
        } catch (BadLocationException e) {
            ;
        }
        String formattedSource = document.get();
        return formattedSource;
    }

    public static void main(String args[]) throws Exception {
        //String code = "int main() { int a; return 0; }";
        InputStream is = new FileInputStream(args[0]);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while(line != null){
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        String code = sb.toString();
        System.out.println(code);
        FormatterTestC cd = new FormatterTestC();
        System.out.println(cd.format(code));
    }
}
