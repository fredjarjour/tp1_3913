// Create the tls program that takes as input the path of a folder that contains organized java test code
// in packages (subfolders, organized according to Java and Maven standards) and output in CSV format (“
// comma separated values, comma separated values) columns

// file path
// packet name 
// class name
// tloc of class
// tassert of class
// tcmp of class tloc/tassert

package com.example;

// import folder 
import java.util.Scanner;


public class tls {
    public static void main(String[] args) {
        Scanner sc = fileReader.getScanner(args);
        if (sc == null) {
            return;
        }



        System.out.println(sc);
        sc.close();

    }
}