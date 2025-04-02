package com.hackademics.util;

import com.hackademics.model.Grade;
import com.hackademics.model.User;

public class GpaCalculator {

    public GpaCalculator(){

    }

    public static double computeGradeAverage(User student) {
        if (student.getGrades() == null
                || student.getGrades().isEmpty()) {
            return 0; // Return 0 if no grades exist
        }
        return student.getGrades().stream()
                .mapToDouble(Grade::getGrade) // Get the grade value from each Grade object
                .average() // Calculate the average
                .orElse(0); // Return 0 if no grades exist
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
