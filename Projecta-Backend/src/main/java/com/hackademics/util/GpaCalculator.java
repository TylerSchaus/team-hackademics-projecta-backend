package com.hackademics.util;

public class GpaCalculator {

    public GpaCalculator(){

    }
    
    public static double convertToGPA(double percentage) {
        double gpa;

        if (percentage < 50) {
            gpa = 0.0;
        } else {
            gpa = 1.0 + (percentage - 50) * (4.3 - 1.0) / (90 - 50);
            gpa = Math.min(gpa, 4.3);
        }

        return Math.round(gpa * 10.0) / 10.0;
    }
}
