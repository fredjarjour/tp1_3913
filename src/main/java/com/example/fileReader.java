package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class fileReader {
    public static Scanner getScanner(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file provided");
            return null;
        }

        try {
            File file = new File(args[0]);
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
            return null;
        }
    }
}
