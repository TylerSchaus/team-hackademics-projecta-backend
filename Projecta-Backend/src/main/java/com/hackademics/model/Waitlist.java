package com.hackademics.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "waitlists")
public class Waitlist {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Long id;

    @Getter
    @Setter
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Getter
    @Setter
    @Column(name = "waitlist_limit", nullable = false)
    private int waitlistLimit;

    @Getter
    @OneToMany(mappedBy = "waitlist", cascade = CascadeType.ALL, orphanRemoval=true)
    private final List<WaitlistEnrollment> waitlistEnrollments = new ArrayList<>();
}