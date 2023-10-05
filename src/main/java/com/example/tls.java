package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.nio.file.Path;
import java.util.Scanner;

public class tls {
    static Pattern p1 = Pattern.compile("assert(Not)?(Equals|Null|Same)(.*)");
    static Pattern p2 = Pattern.compile("assert(ArrayEquals|False|That|Throws|True)(.*)");
    static Pattern p3 = Pattern.compile("fail(.*)");

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java -jar tls.jar <path>");
            return;
        }


        String testDirPath = args[0];
        ArrayList<File> testFiles = getAllFiles(new File(testDirPath));
        ArrayList<File> files = new ArrayList<File>();

        ArrayList<Integer> tlocVals = new ArrayList<Integer>();
        ArrayList<Integer> tassertVals = new ArrayList<Integer>();
        ArrayList<Double> tcmpVals = new ArrayList<Double>();

        for (File file : testFiles) {
            int tassertVal = tassert(file);

            int tlocVal = tloc(file);
            
            files.add(file);
            tlocVals.add(tlocVal);
            tassertVals.add(tassertVal);
            tcmpVals.add((double)tlocVal/tassertVal);
        }


        
        for (int i = 0; i < files.size(); i++) {

            Path filePath = files.get(i).toPath();
            String[] packageAndClassName = getPackageAndClassName(filePath);
            String packageName = packageAndClassName[0];
            String className = packageAndClassName[1];
            String filePathString = filePath.toString().substring(testDirPath.length() + 1);
            String line = filePathString + "," + packageName + "," + className + "," + tlocVals.get(i) + "," + tassertVals.get(i) + "," + tcmpVals.get(i);
        
            System.out.println(line);
        }
    }

    private static ArrayList<File> getAllFiles(File dir) {
        File[] files = dir.listFiles();
        ArrayList<File> allFiles = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory()) {
                allFiles.addAll(getAllFiles(file));
            } else {
                allFiles.add(file);
            }
        }
        return allFiles;
    }

    private static int tloc(File file) {
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            return 0;
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

        return lines;
    }

    private static int tassert(File file) {
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            return 0;
        }

        int assertCount = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (p1.matcher(line).find() || p2.matcher(line).find() || p3.matcher(line).find()) {
                assertCount++;
            }
        }
        sc.close();

        return assertCount;
    }

    public static String[] getPackageAndClassName(Path file) throws Exception {
        Scanner sc;

        sc = new Scanner(file);

        String packageName = "";
        String className = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().startsWith("package")) {
                packageName = line.trim().substring(8, line.trim().length() - 1);
            }
            if (line.trim().startsWith("public class")) {
                className = line.trim().substring(13, line.trim().length() - 1);
                className = className.replaceAll("\\s+","");
            }
            if (!packageName.equals("") && !className.equals("")) {
                break;
            }
        }
        sc.close();

        return new String[] {packageName, className};
    }
}