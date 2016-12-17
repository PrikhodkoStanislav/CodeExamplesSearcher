package ru.compscicenter.practice.searcher.codeformatter;

/**
 * Created by Станислав on 23.11.2016.
 */
public class HandwrittenCodeFormatter {
    public static String format(String sourceString) {
        String result = "";
        int offset = 1;
        char[] chars = sourceString.toCharArray();
        for (char c : chars) {
            result += c;
            if (c == '{') {
                offset *= 4;
                for (int j = 0; j < offset; j++) {
                    result += ' ';
                }
            } else if (c == '}') {
                offset /= 4;
                for (int j = 0; j < offset; j++) {
                    result += ' ';
                }
            }
        }
        return result;
    }
}
