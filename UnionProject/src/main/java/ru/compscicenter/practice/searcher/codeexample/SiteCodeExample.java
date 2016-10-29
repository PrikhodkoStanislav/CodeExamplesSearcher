package ru.compscicenter.practice.searcher.codeexample;

/**
 * Created by user on 14.10.2016!
 */
public class SiteCodeExample extends CodeExample {
    private String siteName;
    private String source;

    public SiteCodeExample(String language, String siteName, String url, String codeExample) {
        this.language = language;
        this.siteName = siteName;
        this.source = url;
        this.codeExample = codeExample;
    }

    public SiteCodeExample(String language, String siteName, String notFound) {
        this.language = language;
        this.siteName = siteName;
        this.codeExample = notFound;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUrl() {
        return source;
    }

    @Override
    public String toString(String format) {
        if ("html".equals(format))
            if (source != null)
                return "<b>Site:</b> <i>" + siteName + "</i><br>" +
                        "<b>Web page:</b> <i>" + source + "</i><br>" +
                        "<b>Example:</b><br>" + codeExample;
            else
                return "<b>Site:</b> " + siteName + "<br>" +
                        codeExample;
        else
            if (source != null)
                return "Site: " + siteName + "\n" +
                        "Web page: " + source + "\n" +
                        "Example:\n" + codeExample;
            else
                return "Site: " + siteName + "\n" +
                        codeExample;
    }
}
