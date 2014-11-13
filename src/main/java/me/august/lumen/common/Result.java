package me.august.lumen.common;

public class Result<O, E> {

    public static <O, E> Result<O, E> ok(O ok) {
        Result<O, E> res = new Result<>();
        res.ok = ok;
        return res;
    }

    public static <O, E> Result<O, E> err(E err) {
        Result<O, E> res = new Result<>();
        res.err = err;
        return res;
    }

    private O ok;
    private E err;

    private Result() {}

    public boolean isOk() {
        return ok != null;
    }

    public boolean isErr() {
        return err != null;
    }

    public O unwrap() {
        if (ok == null) {
            throw new ErrUnwrapException(err);
        } else {
            return ok;
        }
    }

    public E getErr() {
        return err;
    }

    public static class ErrUnwrapException extends RuntimeException {
        public ErrUnwrapException(Object errObj) {
            this("Attempted to unwrap an error value: " + errObj);
        }

        public ErrUnwrapException(String message) {
            super(message);
        }

        public ErrUnwrapException(String message, Throwable cause) {
            super(message, cause);
        }

        public ErrUnwrapException(Throwable cause) {
            super(cause);
        }

        public ErrUnwrapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
