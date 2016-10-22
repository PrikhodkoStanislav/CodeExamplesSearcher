package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by Станислав on 14.10.2016.
 */
public class SelfProjectCodeExample extends CodeExample {
    private long numberOfExample;
    private long strNumber;
    private String pathToTheFile;

    public SelfProjectCodeExample(String pathToTheFile, long numberOfExample, long strNumber, String codeExample) {
        this.pathToTheFile = pathToTheFile;
        this.numberOfExample = numberOfExample;
        this.strNumber = strNumber;
        this.codeExample = codeExample;
    }

    public long getNumberOfExample() {
        return numberOfExample;
    }

    public long getStrNumber() {
        return strNumber;
    }

    public String getPathToTheFile() {
        return pathToTheFile;
    }

    @Override
    public String toString(String format) {
        final String newLine = "%n";
        String result = "Path to the file: " + pathToTheFile + newLine;
        result += "Example " + numberOfExample + " :" + " str " + strNumber + " :" + newLine;
//        result += "----------" + newLine;
        result += codeExample;
//        result += "----------" + newLine;
        result += newLine;
        return result;
    }
}
