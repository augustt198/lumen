package me.august.lumen.compile.error;

public class SourceException extends Exception {

    private int begin = -1;
    private int end   = -1;

    public SourceException() {
        super();
    }

    public SourceException(String message) {
        super(message);
    }

    public SourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceException(Throwable cause) {
        super(cause);
    }

    public SourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SourceException beginPos(int begin) {
        this.begin = begin;
        return this;
    }

    public SourceException endPos(int end) {
        this.end = end;
        return this;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
