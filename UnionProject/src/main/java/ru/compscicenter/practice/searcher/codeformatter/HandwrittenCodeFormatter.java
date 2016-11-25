package ru.compscicenter.practice.searcher.codeformatter;

/**
 * Created by Станислав on 23.11.2016.
 */
public class HandwrittenCodeFormatter {
    public static String format(String sourceString) {
        String result = "";
        int offset = 1;
        char[] chars = sourceString.toCharArray();
        int index = 0;
        while (index < chars.length) {
            char c = chars[index];
            result += c;
            if (c == '{') {
                offset *= 4;
                for (int i = 0; i < offset; i++) {
                    result += ' ';
                }
            } else if (c == '}') {
                offset /= 4;
                for (int i = 0; i < offset; i++) {
                    result += ' ';
                }
            }
        }
        return result;
    }
}
