package ru.compscicenter.practice.searcher;

import ru.compscicenter.practice.searcher.selfProjectSearcher.SelfProjectSearcher;
import ru.compscicenter.practice.searcher.siteSearcher.SiteSearcher;

/**
 * Created by Станислав on 05.10.2016.
 */
public class MainSearcher {
    public static void main(String[] args) {
        Searcher searcher1 = new SiteSearcher();
        Searcher searcher2 = new SelfProjectSearcher();
        System.out.println(searcher1.search(args[0]));
        System.out.println(searcher2.search(args[0], args[1]));
    }
}
