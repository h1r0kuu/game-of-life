package com.h1r0kuu.gameoflife.utils;

import com.h1r0kuu.gameoflife.models.Cell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RLE {
    private static final char ALIVE_CHAR = 'o';
    private static final char DEAD_CHAR = 'b';
    private static final Pattern NUM_PATTERN = Pattern.compile("[0-9]+");

    public static String encode(Cell[][] cells, String rule) {
        StringBuilder sb = new StringBuilder();
        int height = cells.length;
        int width = cells[0].length;

        sb.append(String.format("x = %d, y = %d, rule = %s\n", width, height, rule));
        int runCount = 0;
        char tag = ' ';
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char newTag = cells[i][j].isAlive() ? ALIVE_CHAR : DEAD_CHAR;
                if (tag == ' ') {
                    tag = newTag;
                    runCount = 1;
                } else if (tag == newTag) {
                    runCount++;
                } else {
                    if (runCount > 1) {
                        sb.append(runCount);
                    }
                    sb.append(tag);
                    tag = newTag;
                    runCount = 1;
                }
            }
            if (runCount > 1) {
                sb.append(runCount);
            }
            sb.append(tag);
            tag = ' ';
            if(i != height - 1)
                sb.append("$");
        }
        sb.append("!");
        return sb.toString();
    }

    public static String getName(String content) throws IOException {
        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().startsWith("#N")) {
                return line.split(" ")[1];
            }
        }
        return null;
    }

    public static Cell[][] decode(String rle) {
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(rle);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().startsWith("#") && !line.isEmpty()) {
                lines.add(line);
            }
        }
        String infoLine = lines.get(0);

        String[] subArr = lines.subList(1, lines.size()).toArray(new String[0]);
        String rleLine = String.join("", subArr);

        String[] info = infoLine.split(", ");

        int width = Integer.parseInt(info[0].split("=")[1].trim());
        int height = Integer.parseInt(info[1].split("=")[1].trim());
        if(width == 0 || height == 0) {
            return new Cell[][]{};
        }

        Cell[][] cells = new Cell[height][width];
        int x = 0, y = 0;
        Matcher matcher;
        for(int i = 0; i < rleLine.toCharArray().length; i++) {
            char token = rleLine.charAt(i);
            if (token == '$') {
                if (x < width) {
                    for (; x < width; x++) {
                        cells[y][x] = new Cell(false);
                    }
                }
                y++;
                x = 0;
            } else if (token == '!') {
                if(x < width) {
                    for (; x < width; x++) {
                        cells[y][x] = new Cell(false);
                    }
                }
                break;
            }
            else {
                int runCount = 1;
                matcher = NUM_PATTERN.matcher(rleLine.substring(i));
                if (matcher.find() && matcher.start() == 0) {
                    runCount = Integer.parseInt(matcher.group());
                    i += matcher.group().length();
                    token = rleLine.charAt(i);
                }

                if(token == '$') {
                    if(x < width) {
                        for (; x < width; x++) {
                            cells[y][x] = new Cell(false);
                        }
                    }
                    y++;
                    for (int j = 0; j < runCount; j++) {
                        x = 0;
                        for (; x < width; x++) {
                            cells[y][x] = new Cell(false);
                        }
                        x = 0;
                        if(j != runCount - 1) y++;
                    }
                    continue;
                }

                for (int j = 0; j < runCount; j++) {
                    if (token == DEAD_CHAR) {
                        cells[y][x++] = new Cell(false);
                    } else if (token == ALIVE_CHAR) {
                        cells[y][x++] = new Cell(true);
                    }
                }
            }
        }

        scanner.close();

        return cells;
    }

    public static String read(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}