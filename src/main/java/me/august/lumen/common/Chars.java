package me.august.lumen.common;

public class Chars {

    public static boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public static boolean isLowerAlpha(char c) {
        return c >= 'a' && c <= 'z';
    }

    public static boolean isUpperAlpha(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

}
