package com.hackademics.util;

import java.time.LocalTime;
import java.util.List;

import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.LabSection;

public class ScheduleConflictChecker {
    
    /**
     * Checks for schedule conflicts between a course/lab section and existing enrollments.
     * 
     * @param currentEnrollments List of current enrollments to check against
     * @param course The course to check for conflicts (can be null)
     * @param labSection The lab section to check for conflicts (can be null)
     * @return true if there is a conflict, false otherwise
     */
    public static boolean hasScheduleConflict(List<Enrollment> currentEnrollments, Course course, LabSection labSection) {
        // First check for course conflicts
        if (course != null) {
            for (Enrollment enrollment : currentEnrollments) {
                // Skip if this is the same course (for updating enrollments)
                if (enrollment.getCourse().getId().equals(course.getId())) {
                    continue;
                }
                
                // Check if the days overlap
                if (enrollment.getCourse().getDays().equals(course.getDays())) {
                    // Check for time overlap between courses
                    if (!(enrollment.getCourse().getEndTime().isBefore(course.getStartTime()) ||
                          enrollment.getCourse().getStartTime().isAfter(course.getEndTime()))) {
                        return true;
                    }
                }
            }
        }
        
        // Then check for lab section conflicts if a lab section is provided
        if (labSection != null) {
            for (Enrollment enrollment : currentEnrollments) {
                // Skip if this is the same lab section (for updating enrollments)
                if (enrollment.getLabSection() != null && 
                    enrollment.getLabSection().getId().equals(labSection.getId())) {
                    continue;
                }
                
                // Check for conflicts with existing lab sections
                if (enrollment.getLabSection() != null) {
                    // Check if they're on the same day
                    if (enrollment.getLabSection().getDays() == labSection.getDays()) {
                        // Check for time overlap
                        if (!(enrollment.getLabSection().getEndTime().isBefore(labSection.getStartTime()) ||
                              enrollment.getLabSection().getStartTime().isAfter(labSection.getEndTime()))) {
                            return true;
                        }
                    }
                }
                
                // Check for conflicts with existing courses
                if (enrollment.getCourse().getDays() == labSection.getDays()) {
                    // Check for time overlap between course and lab section
                    if (!(enrollment.getCourse().getEndTime().isBefore(labSection.getStartTime()) ||
                          enrollment.getCourse().getStartTime().isAfter(labSection.getEndTime()))) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Helper method to check if two time periods overlap.
     * 
     * @param start1 Start time of first period
     * @param end1 End time of first period
     * @param start2 Start time of second period
     * @param end2 End time of second period
     * @return true if the periods overlap, false otherwise
     */
    public static boolean doTimePeriodsOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
} 