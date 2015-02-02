package me.august.lumen.compile.scanner.pos;

public class Span implements SourcePositionProvider {

    private int start;
    private int end;

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
}
