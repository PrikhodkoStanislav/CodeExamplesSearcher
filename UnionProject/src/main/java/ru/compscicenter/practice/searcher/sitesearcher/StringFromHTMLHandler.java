package ru.compscicenter.practice.searcher.sitesearcher;

import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

/**
 * Created by user on 26.11.2016!
 */
public class StringFromHTMLHandler extends DefaultHandler {
    private StringBuilder sb = new StringBuilder();

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        sb.append(chars, i, i1);
    }

    public String getCleanedString() {
        return sb.toString();
    }
}
