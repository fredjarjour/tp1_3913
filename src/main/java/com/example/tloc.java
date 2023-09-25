package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class tloc {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java tloc.java <file>");
            return;
        }

        Scanner sc;

        try {
            File file = new File(args[0]);
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
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