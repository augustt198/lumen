package me.august.lumen.compile.analyze;

import java.util.LinkedHashMap;
import java.util.Map;

public class ASTAnnotator<T> {

    private Map<Object, T> map = new LinkedHashMap<>();

    public T getValue(Object obj) {
        return map.get(obj);
    }

    protected void setValue(Object obj, T val) {
        map.put(obj, val);
    }

}
