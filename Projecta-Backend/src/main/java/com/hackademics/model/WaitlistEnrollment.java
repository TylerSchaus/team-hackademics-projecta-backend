package com.hackademics.Model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "waitlist_enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistEnrollment implements Serializable {

    // Composite key fields
    @EmbeddedId
    private WaitlistEnrollmentId id;

    @Column(name = "waitlist_position", nullable = false)
    private int waitlistPosition;

    @ManyToOne
    @MapsId("waitlistId")
    @JoinColumn(name = "waitlist_id")
    private Waitlist waitlist;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    // Composite key class (embedded inside WaitlistEnrollment)
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitlistEnrollmentId implements Serializable {

        @Column(name = "waitlist_id", length = 5)
        private String waitlistId;

        @Column(name = "student_id", length = 9)
        private String studentId;
    }
}