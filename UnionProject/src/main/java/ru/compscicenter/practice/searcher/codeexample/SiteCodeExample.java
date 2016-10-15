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

    public String getSiteName() {
        return siteName;
    }

    public String getUrl(String url) {
        return url;
    }

    @Override
    public String toString() {
        if (url != null)
            return "Site: " + siteName + "\n" +
                    "Web page: " + url + "\n" +
                    "Example:&nbsp;" + codeExample;
        else
            return "Site: " + siteName + "\n" +
                    codeExample;
    }
}
