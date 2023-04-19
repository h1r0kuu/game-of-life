package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.utils.RLE;

import java.util.HashMap;
import java.util.Scanner;

public class Pattern {
    private String name;
    private String author;
    private int width;
    private int height;
    private String rleString;
    private String rule;

    public Pattern(String name, Cell[][] cells) {
        this.name = name;
        this.rleString = RLE.encode(cells, "B3/S23");
    }

    public Pattern(String rleString) {
        Scanner scanner = new Scanner(rleString);
        StringBuilder rleStringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(!line.isEmpty()) {
                line = line.trim();
                if (line.startsWith("#N")) {
                    this.name = line.substring(2).trim();
                } else if(line.startsWith("#O")) {
                    this.author = line.substring(2).trim();
                } else if (!line.startsWith("#")) {
                    rleStringBuilder.append(line).append("\n");
                    if (line.startsWith("x=") || line.startsWith("x =")){
                        String[] info = line.split(", ");
                        this.width = Integer.parseInt(info[0].split("=")[1].trim());
                        this.height = Integer.parseInt(info[1].split("=")[1].trim());
                        this.rule = info[2].split("=")[1].trim();
                    }
                }
            }
        }
        this.rleString = rleStringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRleString() {
        return rleString;
    }

    public String getRule() {
        return rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pattern pattern = (Pattern) o;

        return rleString.equals(pattern.getRleString());
    }


    @Override
    public int hashCode() {
        return rleString.hashCode();
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", rleString='" + rleString + '\'' +
                ", rule='" + rule + '\'' +
                '}';
    }
}