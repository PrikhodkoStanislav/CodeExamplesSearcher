package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by Станислав on 14.10.2016.
 */
public class SelfProjectCodeExample extends CodeExample {
    private long numberOfExample;
    private long strNumber;

    public SelfProjectCodeExample(long numberOfExample, long strNumber) {
        this.numberOfExample = numberOfExample;
        this.strNumber = strNumber;
    }

    public long getNumberOfExample() {
        return numberOfExample;
    }

    public long getStrNumber() {
        return strNumber;
    }

    @Override
    public String toString() {
        return "";
    }
}
