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
        if (args.length != 1 && args.length != 3) {
            System.out.println("Usage 1: java -jar tls.jar <path>");
            System.out.println("Usage 2 to output csv: java -jar tls.jar -o <output_path.csv> <path> ");
            return;
        }

        if (args.length == 3 && (!args[0].equals("-o") || !args[1].endsWith(".csv"))) {
            System.out.println("Usage 1: java -jar tls.jar <path>");
            System.out.println("Usage 2 to output csv: java -jar tls.jar -o <output_path.csv> <path> ");
            return;
        }


        ArrayList<String> output = new ArrayList<String>();
        String testDirPath = args[args.length - 1];
        File testDir = new File(testDirPath);
        if (!testDir.exists()) {
            System.out.println("Error: Path does not exist");
            return;
        }
        ArrayList<File> testFiles = getAllFiles(new File(testDirPath));
        if (testFiles.size() == 0) {
            System.out.println("Error: No test files found");
            return;
        }
        ArrayList<File> files = new ArrayList<File>();

        ArrayList<Integer> tlocVals = new ArrayList<Integer>();
        ArrayList<Integer> tassertVals = new ArrayList<Integer>();
        ArrayList<Double> tcmpVals = new ArrayList<Double>();

        for (File file : testFiles) {
            int tassertVal = tassert(file);

            if (tassertVal == 0) {
                continue;
            }

            int tlocVal = tloc(file);
            
            files.add(file);
            tlocVals.add(tlocVal);
            tassertVals.add(tassertVal);
            tcmpVals.add((double)tlocVal/tassertVal);
        }


        
        for (int i = 0; i < files.size(); i++) {

            Path filePath = files.get(i).toPath();
            String packageName = getPackageName(filePath);
            String className = files.get(i).getName().split("\\.")[0];
            String filePathString = filePath.toString().substring(testDir.toString().length() + 1);
            String line = ".\\"+filePathString + "," + packageName + "," + className + "," + tlocVals.get(i) + "," + tassertVals.get(i) + "," + tcmpVals.get(i);
            output.add(line);

            System.out.println(line);
        }

        if (args.length == 3) {
            String outputPath = args[1];
            File outputFile = new File(outputPath);
            outputFile.createNewFile();
            java.io.FileWriter fw = new java.io.FileWriter(outputFile);
            for (String line : output) {
                fw.write(line + "\n");
            }
            fw.close();
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

    public static String getPackageName(Path file) throws Exception {
        Scanner sc;

        sc = new Scanner(file);

        String packageName = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().startsWith("package")) {
                packageName = line.trim().split(" ")[1];
                break;
            }
        }
        sc.close();

        if (packageName.endsWith(";")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        return packageName;
    }
}