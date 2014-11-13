package me.august.lumen.common;

import java.util.List;

public class Lists {

    public static <T> T[] arr(List<T> list) {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[list.size()];

        return list.toArray(arr);
    }

    private Lists() {}
}
