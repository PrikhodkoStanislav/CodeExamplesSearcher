package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.sitesearcher.CodeFormatter;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Станислав on 05.10.2016.
 */
public class MainSearcher {
    private static CodeFormatter codeFormatter = CodeFormatter.getInstance();

    public static void main(String[] args) {
        List<CodeExample> l1;
        String s2 = "";
        Searcher searcher1 = new SiteSearcher();
        SelfProjectSearcher searcher2 = new SelfProjectSearcher();
        l1 = searcher1.search(args[0]);
        if (args.length > 1) {
            s2 = searcher2.search(args[0], args[1]);
            //TODO method return List<CodeExample>
            //l1.addAll(l2);
//            s2 = searcher2.search("strlen", "../UnionProject/src/main/resources");
//            for (CodeExample s  : searcher2.list) {
//                System.out.println(s.toString());
//            }
//            CodeDuplicateRemover.run(searcher2.list);
//            System.out.println(s2);
        }

        //TODO remove duplicates

        for (CodeExample codeExample : l1) {
            codeExample.setCodeExample(codeFormatter.toPrettyCode(codeExample.codeExample));
        }

        codeFormatter.createHTML(l1);

        if (args.length > 1) {
            String path2 = "examplesFromProject.html";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path2))) {
                writer.write(s2);
                writer.newLine();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File htmlFile2 = new File(path2);
            try {
                Desktop.getDesktop().browse(htmlFile2.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
