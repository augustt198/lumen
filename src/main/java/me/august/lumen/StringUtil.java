package me.august.lumen;

public class StringUtil {

    private StringUtil() {}

    // fastest string repetition method I could think of
    public static String repeat(String str, int rep) {
        char[] src = str.toCharArray();
        char[] res = new char[str.length() * rep];

        for (int i = 0; i < rep; i++) {
            System.arraycopy(src, 0, res, i * src.length, src.length);
        }

        return new String(res);
    }

    public static String repeat(char chr, int rep) {
        char[] res = new char[rep];
        for (int i = 0; i < rep; i++)
            res[i] = chr;

        return new String(res);
    }

}
