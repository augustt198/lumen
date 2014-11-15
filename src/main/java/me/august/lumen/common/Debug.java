package me.august.lumen.common;

public class Debug {

    private static boolean enabled;

    public static void log(Object... objs) {
        if (enabled) {
            for (Object o : objs) System.out.println(o);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        Debug.enabled = enabled;
    }

}
