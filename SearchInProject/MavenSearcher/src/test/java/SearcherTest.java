import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Станислав on 01.10.2016.
 */
public class SearcherTest {

    Searcher searcher = new Searcher();

    @Test
    public void testEmptyDirectory() {
        assertEquals("", searcher.search("someFunction", "../emptyDirectory"));
    }

    @Test
    public void testEmptyFile() {
        assertEquals("", searcher.search("someFunction", "../emptyFile.txt"));
    }

    @Test
    public void testFile() {
        assertEquals("Example 1 : str 2 :\n" +
                "----------\n" +
                "for (int i = 0; i < n; i++) {\n" +
                "^^^^^^^^^^\n" +
                "\ttheFunction(someArguments[i]);\n" +
                "^^^^^^^^^^\n" +
                "}\n" +
                "----------\n\n", searcher.search("theFunction", "../file.txt"));
    }
}