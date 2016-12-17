package ru.compscicenter.practice.searcher.codeformatter;

/**
 * Created by Станислав on 09.12.2016.
 */
public class AStyleFormatter {
    public static String format(String code) throws Exception
    {
        return (String) Class.forName("AStyleInterface").getDeclaredMethod("m", String.class).invoke(null, code);
    }
}
