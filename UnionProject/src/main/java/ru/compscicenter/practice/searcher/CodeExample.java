package ru.compscicenter.practice.searcher;

/**
 * Created by user on 14.10.2016!
 */
public class CodeExample {
    protected String codeExample;
    protected String language;
    protected String function;
    protected String source;

    public String toString(String format) {
        return "";
    };

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCodeExample() {
        return codeExample;
    }

    public void setCodeExample(String codeExample) {
        this.codeExample = codeExample;
    }
}
