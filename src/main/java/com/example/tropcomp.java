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
        File[] files = new File[testFiles.size()];
        files = testFiles.toArray(files);

        int[] tlocVals = new int[files.length];
        int[] tassertVals = new int[files.length];
        float[] tcmpVals = new float[files.length];

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Scanner sc;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                continue;
            }

            int tlocVal = 0;
            int tassertVal = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                tlocVal += tloc(line);
                tassertVal += tassert(line);
            }

            tlocVals[i] = tlocVal;
            tassertVals[i] = tassertVal;
            if (tassertVal == 0) {
                tcmpVals[i] = Float.MAX_VALUE;
            } else {
                tcmpVals[i] = tlocVal/tassertVal;
            }

            sc.close();
        }

        int[] tlocValsSorted = tlocVals.clone();
        float[] tcmpValsSorted = tcmpVals.clone();
        Arrays.sort(tlocValsSorted);
        Arrays.sort(tcmpValsSorted);

        int index = (int) Math.floor(files.length * threshold/100);
        int tlocVal = tlocValsSorted[index];
        float tcmpVal = tcmpValsSorted[index];
        
        for (int i = 0; i < files.length; i++) {
            if (tlocVals[i] >= tlocVal && tcmpVals[i] >= tcmpVal) {
                System.out.println(tls(files[i], tlocVals[i], tassertVals[i], tcmpVals[i]));
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

    private static int tloc(String line) {
        if (line.trim().length() > 0 && !line.trim().startsWith("//")) {
            return 1;
        }
        return 0;
    }

    private static int tassert(String line) {
        if (p1.matcher(line).find() || p2.matcher(line).matches() || p3.matcher(line).matches()) {
            return 1;
        }
        return 0;
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
