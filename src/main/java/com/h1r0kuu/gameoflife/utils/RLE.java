package com.h1r0kuu.gameoflife.utils;

import com.h1r0kuu.gameoflife.entity.Cell;

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

    public static String encode(Cell[][] cells) {
        StringBuilder sb = new StringBuilder();
        int height = cells.length;
        int width = cells[0].length;

        sb.append(String.format("x = %d, y = %d, rule = B3/S23, ", width, height));

        int runCount = 0;
        char tag = ' ';
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
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
            sb.append("$");
        }
        sb.append("!");
        return sb.toString();
    }

    public static Cell[][] decode(String rle) {
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(rle);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.startsWith("#")) {
                lines.add(line);
            }
        }

        String[] parts = rle.split(", ");
        int width = Integer.parseInt(parts[0].substring(4));
        int height = Integer.parseInt(parts[1].substring(4));
        Cell[][] cells = new Cell[height][width];
        int x = 0, y = 0;
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher;
        for(int i = 0; i < parts[3].toCharArray().length; i++) {
            char token = parts[3].charAt(i);
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
            } else if(token == '2' && parts[3].charAt(i + 1) == '$') {
                if(x < width) {
                    for (; x < width; x++) {
                        cells[y][x] = new Cell(false);
                    }
                }
                y++;
                x = 0;
                for (; x < width; x++) {
                    cells[y][x] = new Cell(false);
                }
                y++;
                i++;
                x = 0;
            } else {
                int runCount = 1;
                matcher = pattern.matcher(parts[3].substring(i));
                if (matcher.find() && matcher.start() == 0) {
                    runCount = Integer.parseInt(matcher.group());
                    i += matcher.group().length();
                    token = parts[3].charAt(i);
                }

                for (int j = 0; j < runCount; j++) {
                    if (token == DEAD_CHAR) {
                        cells[y][x++] = new Cell(false);
                    } else if (token == ALIVE_CHAR) {
                        cells[y][x++] = new Cell(true);
                    } else {
                        throw new IllegalArgumentException("Invalid RLE tag: " + token);
                    }
                }
            }
        }

        scanner.close();

        return cells;
    }

    public static Cell[][] read(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        String rle = sb.toString();
        return decode(rle);
    }
}