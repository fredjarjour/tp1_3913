package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
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
        File[] files = new File(testDirPath).listFiles();

        int[] tlocVals = new int[files.length];
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
                System.out.println(tls(files[i]));
            }
        }
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

    private static String tls(File file) throws Exception {

        Path filePath = file.toPath();  
        
        String packageName = getPackageName(filePath);
        String className = getClassName(filePath);
        int lines = getLines(filePath);
        int asserts = getAsserts(filePath);
        double tcmp = (double) lines / asserts;
        String line = filePath.toString() + "," + packageName + "," + className + "," + lines + "," + asserts + "," + tcmp;
        
        return line;
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
