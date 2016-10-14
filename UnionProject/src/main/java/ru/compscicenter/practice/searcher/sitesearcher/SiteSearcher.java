package ru.compscicenter.practice.searcher.sitesearcher;

import ru.compscicenter.practice.searcher.CodeExamplesStorage;
import ru.compscicenter.practice.searcher.Searcher;
import ru.compscicenter.practice.searcher.codeexample.CodeExample;

import java.util.List;

/**
 * Created by user on 05.10.2016!
 */
public class SiteSearcher extends Searcher {

    @Override
    public String search(String methodNameQuery) {
        SiteProcessor[] processors = {new CPlusPlusSiteProcessor(), new CPPReferenceSiteProcessor()};

        for (SiteProcessor processor : processors) {
            processor.setQuery(methodNameQuery);
            processor.start();
        }

        for (SiteProcessor processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>").append("<br>")
                .append("<html>").append("<br>");
        sb.append("<h3>Examples of this method usage from sites:</h3>").append("<br>");
        sb.append("<body>");
        List<CodeExample> answers = CodeExamplesStorage.getInstance().getExamples();
        if (answers != null)
            for (CodeExample answer : answers) {
                sb.append("<div clas=\"pretty\">").append(answer.toString()).append("</div>").append("<br>");
            }
        else
            sb.append("<p>Sorry! Connection was interrupted! :(<p>").append("<br>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
