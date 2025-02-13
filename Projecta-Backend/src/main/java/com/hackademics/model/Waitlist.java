package com.hackademics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waitlists")
public class Waitlist {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Long waitlistId;

    @Getter
    @Setter
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Getter
    @Setter
    @Column(name = "waitlist_limit", nullable = false)
    private int waitlistLimit;
}