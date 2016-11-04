package ru.compscicenter.practice.searcher.database;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.io.Serializable;

/**
 * Created by user on 28.10.2016!
 */
@Entity
public class CodeExampleEntity implements Serializable {
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String language;
    private String functionName;
    private String source;

    // Primary key is the example
    // This assumes that the example is
    // unique in the database.
    @PrimaryKey
    private String example;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
