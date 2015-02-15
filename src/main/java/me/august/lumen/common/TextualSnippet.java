package me.august.lumen.common;

import me.august.lumen.compile.scanner.pos.Span;

import java.util.HashMap;
import java.util.Map;

public class TextualSnippet {

    private Map<Integer, String> lines;
    private Span selected;

    public TextualSnippet(Map<Integer, String> lines, Span selected) {
        this.lines = lines;
        this.selected = selected;
    }

    /**
     * Creates a {@code TextualSnippet} by selecting lines
     * within the range {@code start} to {@code end}.
     * @param str The source string
     * @param start The starting index of the selection (inclusive)
     * @param end The ending index of the selection (exclusive)
     * @return A {@code TextualSnippet} based on the selection range
     */
    public static TextualSnippet selectLines(String str, int start, int end) {
        Map<Integer, String> lines = new HashMap<>();

        int currentLine = 1;
        int lineStartPos = 0;

        int relativeStartPos  = 0;

        for (int i = 0; i < start; i++) {
            if (str.charAt(i) == '\n') {
                currentLine++;
                lineStartPos = i + 1;
            }
            if (i == start - 1) {
                relativeStartPos = i - lineStartPos + 1;
            }
        }

        int relativeEndPos = 0;

        for (int i = start; i < str.length(); i++) {
            if (i == end) {
                relativeEndPos = i - lineStartPos + 1;
            }
            if (str.charAt(i) == '\n' || i == str.length() - 1) {
                String sub = str.substring(lineStartPos, i);
                lines.put(currentLine, sub);

                currentLine++;
                lineStartPos = i + 1;

                if (i >= end) {
                    break;
                }
            }
        }

        Span selected = new Span(relativeStartPos, relativeEndPos);
        return new TextualSnippet(lines, selected);
    }
}
