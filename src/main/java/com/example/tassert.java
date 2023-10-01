package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class tassert {
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

        Pattern p1 = Pattern.compile("assert(Not)?(Equals|Null|Same)(.*)");
        Pattern p2 = Pattern.compile("assert(ArrayEquals|False|That|Throws|True)(.*)");
        Pattern p3 = Pattern.compile("fail(.*)");

        int assertCount = 0;
        int lineNum = 1;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (p1.matcher(line).find() || p2.matcher(line).find() || p3.matcher(line).find()) {
                System.out.println(lineNum + ": " + line);
                assertCount++;
            }
            lineNum++;
        }
        sc.close();

        System.out.println(assertCount);
    }
}
