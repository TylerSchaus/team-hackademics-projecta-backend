package com.hackademics.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "waitlist_enrollments")
public class WaitlistEnrollment implements Serializable {

    // Composite key fields
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private int waitlistPosition;

    @Getter
    @Setter
    @JoinColumn(name = "waitlist_id", nullable =false)
    private Long waitlistId; 

    @Getter
    @Setter
    @JoinColumn(name = "student_id")
    private Long studentId;

}