package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by user on 14.10.2016!
 */
public class SiteCodeExample extends CodeExample {
    private String siteName;
    private String url;

    public SiteCodeExample(String siteName, String url, String codeExample) {
        this.siteName = siteName;
        this.url = url;
        this.codeExample = codeExample;
    }

    public SiteCodeExample(String siteName, String notFound) {
        this.siteName = siteName;
        this.codeExample = notFound;
    }

    @Override
    public String toString(String format) {
        if ("html".equals(format))
            if (url != null)
                return "<b>Site:</b> <i>" + siteName + "</i><br>" +
                        "<b>Web page:</b> <i>" + url + "</i><br>" +
                        "<b>Example:</b><br>" + codeExample;
            else
                return "<b>Site:</b> " + siteName + "<br>" +
                        codeExample;
        else
            if (url != null)
                return "Site: " + siteName + "\n" +
                        "Web page: " + url + "\n" +
                        "Example:\n" + codeExample;
            else
                return "Site: " + siteName + "\n" +
                        codeExample;
    }
}
