package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.codeduplicateremover.CodeDuplicateRemover;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;
import ru.compscicenter.practice.searcher.selfprojectsearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.sitesearcher.SiteSearcher;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Станислав on 05.10.2016.
 */
public class MainSearcher {
    public static void main(String[] args) {
        String s1 = "";
        String s2 = "";
        Searcher searcher1 = new SiteSearcher();
        SelfProjectSearcher searcher2 = new SelfProjectSearcher();
        s1 = searcher1.search(args[0]);
        if (args.length > 1) {
            s2 = searcher2.search(args[0], args[1]);
//            s2 = searcher2.search("strlen", "../UnionProject/src/main/resources");
//            for (CodeExample s  : searcher2.list) {
//                System.out.println(s.toString());
//            }
//            CodeDuplicateRemover.run(searcher2.list);
//            System.out.println(s2);
        }

        String path = "examples.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(s1);
//            writer.write(s2);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File htmlFile = new File(path);
        try {
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
