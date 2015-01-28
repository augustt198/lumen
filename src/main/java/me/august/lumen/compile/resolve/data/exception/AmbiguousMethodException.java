package me.august.lumen.compile.resolve.data.exception;

import me.august.lumen.compile.resolve.data.MethodData;

import java.util.List;

public class AmbiguousMethodException extends Exception {

    private List<MethodData> applicableMethods;

    public AmbiguousMethodException(List<MethodData> applicableMethods) {
        this.applicableMethods = applicableMethods;
    }

    public AmbiguousMethodException(String message, List<MethodData> applicableMethods) {
        super(message);
        this.applicableMethods = applicableMethods;
    }

    public List<MethodData> getApplicableMethods() {
        return applicableMethods;
    }
}
