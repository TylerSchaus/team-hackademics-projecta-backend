package com.hackademics.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

@Entity
@Table(name = "waitlists")
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "waitlist_limit", nullable = false)
    private int waitlistLimit;

    @OneToMany(mappedBy = "waitlist", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final List<WaitlistEnrollment> waitlistEnrollments = new ArrayList<>();

    private String term;

    // Constructor

    public Waitlist(){}

    public Waitlist (Course course, int waitlistLimit){
        this.course = course;
        this.waitlistLimit = waitlistLimit;
        this.term = course.getTerm(); 
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getWaitlistLimit() {
        return waitlistLimit;
    }

    public void setWaitlistLimit(int waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }

    public List<WaitlistEnrollment> getWaitlistEnrollments() {
        return waitlistEnrollments;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}