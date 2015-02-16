package me.august.lumen.common;

import me.august.lumen.compile.scanner.pos.Span;
import org.fusesource.jansi.Ansi;

import java.util.Map;
import java.util.TreeMap;

import static org.fusesource.jansi.Ansi.Attribute.*;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class TextualSnippet {

    private TreeMap<Integer, String> lines;
    private Span selected;

    public TextualSnippet(TreeMap<Integer, String> lines, Span selected) {
        this.lines = lines;
        this.selected = selected;
    }

    public void printError(String message) {
        Ansi ansi;

        ansi = ansi()
                .a(INTENSITY_BOLD).fg(RED)
                .a("ERROR: " + message).reset();
        System.out.println(ansi);

        int ljust = String.valueOf(lines.lastKey()).length();
        String format = "%" + ljust + "s";

        int count = 0;
        for (Map.Entry<Integer, String> line : lines.entrySet()) {
            String content = line.getValue();

            String first;
            String middle;
            String last;
            String extra = null;

            if (count == 0) {
                first = content.substring(0, selected.getStart());
                if (count == lines.size() - 1) {
                    middle = content.substring(selected.getStart(), selected.getEnd() - 1);
                    last = content.substring(selected.getEnd() - 1);
                    extra = underline(
                            ljust + 3 + selected.getStart(),
                            ljust + 3 + selected.getEnd()
                    ).toString();
                } else {
                    middle = content.substring(selected.getStart());
                    last = "";
                }
            } else if (count == lines.size() - 1) {
                first = "";
                middle = content.substring(0, selected.getEnd());
                last = content.substring(selected.getEnd());

            } else {
                first = "";
                middle = content;
                last = "";
            }

            String justified = String.format(format, line.getKey());
            ansi = ansi().
                    a(INTENSITY_BOLD).a(justified + ": ").reset()
                    .a(first)
                    .a(INTENSITY_BOLD).fg(RED).a(middle).reset()
                    .a(last);

            System.out.println(ansi);

            if (extra != null) {
                System.out.println(extra);
            }

            count++;
        }
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
        TreeMap<Integer, String> lines = new TreeMap<>();

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

    private static Ansi underline(int start, int end) {
        String lead = StringUtil.repeat(' ', start - 1);
        String underline = StringUtil.repeat('^', end - start - 1);

        return ansi().a(lead).a(INTENSITY_BOLD).fg(RED).a(underline).reset();
    }

}
