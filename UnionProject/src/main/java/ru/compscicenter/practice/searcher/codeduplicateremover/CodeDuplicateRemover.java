package ru.compscicenter.practice.searcher.codeduplicateremover;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.*;


/**
 * Created by Станислав on 15.10.2016.
 */
public class CodeDuplicateRemover {
    public CodeDuplicateRemover(List<CodeExample> list) {

    }

    public static void run() {
        CLexer lexer = new CLexer(new ANTLRFileStream("add.c"));
        CParser parser = new CParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        CNewVisitor visitor = new CNewVisitor();
        visitor.visit(tree);
    }
}
