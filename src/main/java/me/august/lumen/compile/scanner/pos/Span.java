package me.august.lumen.compile.scanner.pos;

public class Span implements SourcePositionProvider {

    private int start;
    private int end;

    public Span() {
        this(0, 0);
    }

    public Span(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Span(SourcePositionProvider src) {
        this.start = src.getStart();
        this.end   = src.getEnd();
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Span{" + start + "..." + end + "}";
    }
}
