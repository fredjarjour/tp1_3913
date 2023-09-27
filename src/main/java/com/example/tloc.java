package com.example;

import java.util.Scanner;

public class tloc {
    public static void main(String[] args) {
        Scanner sc = fileReader.getScanner(args);
        if (sc == null) {
            return;
        }

        int lines = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().length() > 0 && !line.trim().startsWith("//")) {
                lines++;
            }
        }
        sc.close();

        System.out.println(lines);
    }
}