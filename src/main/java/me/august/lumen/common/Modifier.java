package me.august.lumen.common;

import java.util.ArrayList;
import java.util.List;

public enum Modifier {

    PUBLIC(0x0001),
    PRIVATE(0x0002),
    PROTECTED(0x004),
    PACKAGE_PRIVATE(0x0000),

    FINAL(0x0010),

    STATIC(0x0008),
    ABSTRACT(0x0400),

    VOLATILE(0x0040),
    SYNCHRONIZED(0x0020),
    NATIVE(0x0100);

    private int value;

    Modifier(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int compose(Modifier... mods) {
        int res = 0;
        for (Modifier m : mods) {
            res |= m.value;
        }
        return res;
    }

    public static Modifier[] fromAccess(int access) {
        List<Modifier> modifiers = new ArrayList<>();
        for (Modifier mod : values()) {
            if ((access & mod.getValue()) == mod.getValue()) {
                modifiers.add(mod);
            }
        }
        return modifiers.toArray(new Modifier[modifiers.size()]);
    }
}
