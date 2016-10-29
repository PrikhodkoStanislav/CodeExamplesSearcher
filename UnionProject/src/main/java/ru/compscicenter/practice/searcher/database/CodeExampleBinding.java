package ru.compscicenter.practice.searcher.database;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Created by user on 29.10.2016!
 */
public class CodeExampleBinding extends TupleBinding<CodeExampleEntity> {
    @Override
    public CodeExampleEntity entryToObject(TupleInput tupleInput) {
        String language = tupleInput.readString();
        String functionName = tupleInput.readString();
        String source = tupleInput.readString();
        String example = tupleInput.readString();

        CodeExampleEntity entity = new CodeExampleEntity();
        entity.setLanguage(language);
        entity.setFunctionName(functionName);
        entity.setSource(source);
        entity.setExample(example);

        return entity;
    }

    @Override
    public void objectToEntry(CodeExampleEntity codeExampleEntity, TupleOutput tupleOutput) {
        tupleOutput.writeString(codeExampleEntity.getLanguage());
        tupleOutput.writeString(codeExampleEntity.getFunctionName());
        tupleOutput.writeString(codeExampleEntity.getSource());
        tupleOutput.writeString(codeExampleEntity.getExample());
    }
}
