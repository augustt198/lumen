package me.august.lumen.compile.analyze;

import java.util.LinkedHashMap;
import java.util.Map;

public class ASTAnnotator<T> {

    private Map<Object, T> map = new LinkedHashMap<>();

    public T getValue(Object obj) {
        return map.get(obj);
    }

    public T getValueOrFail(Object obj) {
        T val = getValue(obj);
        if (val == null) {
            throw new RuntimeException("Could not find value for : " + obj);
        } else {
            return val;
        }
    }

    public T getValueOrFail(Object obj, RuntimeException ex) {
        T val = getValue(obj);
        if (val == null) {
            throw ex;
        } else {
            return val;
        }
    }

    protected void setValue(Object obj, T val) {
        map.put(obj, val);
    }

}
