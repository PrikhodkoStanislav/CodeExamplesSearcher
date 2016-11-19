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
    private long id;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String codeExample;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String language;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String function;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String source;
    private long modificationDate;
    private long lineWithFunction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(long modificationDate) {
        this.modificationDate = modificationDate;
    }

    public long getLineWithFunction() {
        return lineWithFunction;
    }

    public void setLineWithFunction(long lineWithFunction) {
        this.lineWithFunction = lineWithFunction;
    }

}
