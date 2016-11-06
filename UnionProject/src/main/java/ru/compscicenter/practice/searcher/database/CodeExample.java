package ru.compscicenter.practice.searcher.database;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.io.Serializable;

/**
 * Created by user on 14.10.2016!
 */
@Entity
public class CodeExample implements Serializable {
    @PrimaryKey
    protected String codeExample;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    protected String language;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    protected String function;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    protected String source;

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
