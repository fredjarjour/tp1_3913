package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class tassert {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar tassert.jar <path>");
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
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (p1.matcher(line).find() || p2.matcher(line).find() || p3.matcher(line).find()) {
                if (!line.trim().startsWith("import")) {
                    assertCount++;
                }
            }
        }
        sc.close();

        System.out.println(assertCount);
    }
}
