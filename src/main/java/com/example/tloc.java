package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class tloc {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file provided");
            return;
        }

        Scanner sc;

        try {
            File file = new File(args[0]);
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
            return;
        }

        int lines = 0;
        boolean comment = false;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().startsWith("/*")) {
                if (line.contains("*/") && !line.trim().endsWith("*/")) {
                    lines++;
                    continue;
                } else if (line.endsWith("*/")) {
                    continue;
                }
                comment = true;
                continue;
            }
            if (comment && line.trim().endsWith("*/")) {
                comment = false;
                continue;
            }
            if (line.trim().length() > 0 && !line.trim().startsWith("//") && !comment) {
                lines++;
            }
        }
        sc.close();

        System.out.println(lines);
    }
}