package me.august.lumen.compile.scanner;

public class Token {

    private String content;

    private int start;  // start of token in source code (inclusive)
    private int end;    // end of token in source code (exclusive)

    private Type type;

    public Token(String content, int start, int end, Type type) {
        this.content = content;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Type getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
            "content='" + content + '\'' +
            ", start=" + start +
            ", end=" + end +
            ", type=" + type +
            '}';
    }
}
