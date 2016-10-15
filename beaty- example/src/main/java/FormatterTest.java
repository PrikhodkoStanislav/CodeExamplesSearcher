import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
 
public class FormatterTest {
 
    public static void main(String[] args) {
        String code = "public class TestFormatter{public static void main(String[] args){System.out.println(\"Hello World\");}}";
        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
 
        TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);
        IDocument doc = new Document(code);
        try {
            textEdit.apply(doc);
            System.out.println(doc.get());
        } catch (MalformedTreeException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
