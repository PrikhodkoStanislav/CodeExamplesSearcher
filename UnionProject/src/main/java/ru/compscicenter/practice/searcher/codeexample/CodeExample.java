package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by user on 14.10.2016!
 */
public abstract class CodeExample {
    public String codeExample;

    public abstract String toString(String format);

    public void setCodeExample(String codeExample) {
        this.codeExample = codeExample;
    }

    public String getCodeExample() {
        return codeExample;
    }
}
