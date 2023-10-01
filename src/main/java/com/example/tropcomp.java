package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class tropcomp {

    static Pattern p1 = Pattern.compile("assert(Not)?(Equals|Null|Same)\\(.*\\)");
    static Pattern p2 = Pattern.compile("assert(ArrayEquals|False|That|Throws|True)\\(.*\\)");
    static Pattern p3 = Pattern.compile("fail\\(.*\\)");

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java tropcomp.java <path> <threshold>");
            return;
        }

        double threshold;
        try {
            threshold = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: Threshold must be a number");
            return;
        }

        String testDirPath = args[0] + File.separator + "src" + File.separator + "test" + File.separator + "java";
        ArrayList<File> testFiles = getAllFiles(new File(testDirPath));
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

        int[] tlocValsSorted = tlocVals.stream().mapToInt(i -> i).toArray();
        double[] tcmpValsSorted = tcmpVals.stream().mapToDouble(i -> i).toArray();
        Arrays.sort(tlocValsSorted);
        Arrays.sort(tcmpValsSorted);

        int index = (int) Math.floor(files.size() * (1 - threshold/100));
        int tlocVal = tlocValsSorted[index];
        double tcmpVal = tcmpValsSorted[index];
        
        for (int i = 0; i < files.size(); i++) {
            if (tlocVals.get(i) >= tlocVal && tcmpVals.get(i) >= tcmpVal) {
                System.out.println(tls(files.get(i), tlocVals.get(i), tassertVals.get(i), tcmpVals.get(i)));
            }
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
            if (p1.matcher(line).find() || p2.matcher(line).matches() || p3.matcher(line).matches()) {
                assertCount++;
            }
        }
        sc.close();

        return assertCount;
    }

    private static String tls(File file, int lines, int asserts, double tcmp) throws Exception {

        Path filePath = file.toPath();
        String[] packageAndClassName = getPackageAndClassName(filePath);
        String packageName = packageAndClassName[0];
        String className = packageAndClassName[1];
        String line = filePath.toString() + "," + packageName + "," + className + "," + lines + "," + asserts + "," + tcmp;
        
        return line;
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
            }
            if (!packageName.equals("") && !className.equals("")) {
                break;
            }
        }
        sc.close();

        return new String[] {packageName, className};
    }
}
