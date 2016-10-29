package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by user on 14.10.2016!
 */
public abstract class CodeExample {
    protected String codeExample;
    protected String language;
    protected String source;

    public abstract String toString(String format);

    public void setCodeExample(String codeExample) {
        this.codeExample = codeExample;
    }

    public String getCodeExample() {
        return codeExample;
    }

    public String getLanguage() {
        return language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
