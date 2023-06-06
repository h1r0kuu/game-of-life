package com.h1r0kuu.gameoflife.utils;

public class LabelUtility {
    public static final String GENERATION_COUNTER = "T %d";
    public static final String GAME_SPEED = "%d gps";
    public static final String CELL_INFO = "%d,%d=%s";
    public static final String RANDOM_PERCENTAGE = "%d %%";

    public static String getText(String value, String format) {
        return String.format(format, value);
    }

    public static String getText(int value, String format) {
        return String.format(format, value);
    }

    public static String getText(String format, Object... values) {
        return String.format(format, values);
    }
}
