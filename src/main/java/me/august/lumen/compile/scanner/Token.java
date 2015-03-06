package me.august.lumen.compile.scanner;

public class Token {

    private String content;

    private int start;  // start of token in source code (inclusive)
    private int end;    // end of token in source code (exclusive)

    private TokenType type;

    public Token(String content, int start, int end, TokenType type) {
        this.content = content;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public Token expectType(TokenType type) {
        return expectType(type, "Expected token type " + type + ", found " + this.type);
    }

    public Token expectType(TokenType type, String msg) {
        if (this.type != type) {
            throw new RuntimeException(msg);
        }
        return this;
    }

    public Token expectContent(String content) {
        return expectContent(content, "Expected content: " + content + ", found: " + this.content);
    }

    public Token expectContent(String content, String msg) {
        if (!this.content.equals(content)) {
            throw new RuntimeException(msg);
        }
        return this;
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

    public TokenType getType() {
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

    public void setType(TokenType type) {
        this.type = type;
    }

    public boolean hasAttribute(TokenType.Attribute a) {
        return type.hasAttribute(a);
    }

    public boolean isModifier() {
        return type.isModifier();
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
