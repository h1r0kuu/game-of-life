package com.h1r0kuu.gameoflife.utils;

public class LabelUtility {
    public static final String FPS = "%d FPS";
    public static final String GENERATION_COUNTER = "Generation: %d";
    public static final String GAME_SPEED = "%d gps";

    public static String getText(String value, String format) {
        return String.format(format, value);
    }

    public static String getText(int value, String format) {
        return String.format(format, value);
    }
}
