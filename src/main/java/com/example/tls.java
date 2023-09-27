package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class tls {
    public static void main(String[] args) throws Exception {
        if (args.length != 1 && args.length != 3) {
            System.out.println("Error: Wrong format." + args.length);
            return;
        }
        String folder = "";
        String outputFile = "";
        // check if output file is provided
        if (args.length == 3) {
            if (args[0].equals("-o")) {
                if (!args[1].endsWith(".csv")) {
                    return;
                }
                outputFile = args[1];
                folder = args[2];
            }else{
               System.out.println("Error: Wrong format.");
               return;
            }

        }else{
            folder = args[0];
        }
        
        Path rootPath = Paths.get(folder);

        if (!Files.isDirectory(rootPath)) {
            System.out.println("Error: The specified folder does not exist.");
            return;
        }

        List<Path> files = Files.walk(rootPath)
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        
        //create a list of comma seperated values containing: the path, the package name, the class name, and the number of lines of code
        String[] csv = new String[files.size()];

        for (Path file : files) {
            String packageName = getPackageName(file);
            String className = getClassName(file);
            int lines = getLines(file);
            int asserts = getAsserts(file);
            double tcmp = (double) lines / asserts;
            int index = files.indexOf(file);
            file = rootPath.relativize(file);
            String line = file.toString() + "," + packageName + "," + className + "," + lines + "," + asserts + "," + tcmp;
            csv[index] = line;
        }
        
        for (String line : csv) {
            System.out.println(line);
        }

        //write the csv to a file
        if (args.length == 3) {
            File file = new File(outputFile);
            file.createNewFile();
            java.io.FileWriter writer = new java.io.FileWriter(file);
            for (String line : csv) {
                writer.write(line + "\n");
            }
            writer.close();
           
        }


    }

    public static String getPackageName(Path file) throws Exception {
        Scanner sc;

        sc = new Scanner(file);

        String packageName = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().startsWith("package")) {
                packageName = line.trim().substring(8, line.trim().length() - 1);
                break;
            }
        }
        sc.close();

        return packageName;
    }

    public static String getClassName(Path file) throws Exception {
        Scanner sc;

        sc = new Scanner(file);

        String className = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().startsWith("public class")) {
                className = line.trim().substring(13, line.trim().length() - 1);
                break;
            }
        }
        sc.close();

        return className;
    }

    public static int getLines(Path file) throws Exception{
        Scanner sc;
       
        sc = new Scanner(file);

        int lines = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().length() > 0 && !line.trim().startsWith("//")) {
                lines++;
            }
        }
        sc.close();

        return lines;
    }

     public static int getAsserts(Path file) throws Exception{
        Scanner sc;
        
        sc = new Scanner(file);
       
        Pattern p1 = Pattern.compile("assert(Not)?(Equals|Null|Same)\\(.*\\)");
        Pattern p2 = Pattern.compile("assert(ArrayEquals|False|That|Throws|True)\\(.*\\)");
        Pattern p3 = Pattern.compile("fail\\(.*\\)");

        int assertCount = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (p1.matcher(line).find() || p2.matcher(line).matches() || p3.matcher(line).matches()) {
                assertCount++;
            }
        }
        sc.close();

        return assertCount;
     }
}