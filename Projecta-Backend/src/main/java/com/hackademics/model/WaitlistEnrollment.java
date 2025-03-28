package com.hackademics.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "waitlist_enrollments")
public class WaitlistEnrollment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private Long id;

    @Column(nullable = false)
    private int waitlistPosition;

    @ManyToOne
    @JoinColumn(name = "waitlist_id", nullable =false)
    private Waitlist waitlist; 

    @ManyToOne
    @JoinColumn(name = "student_reference", nullable = false)
    private User student;

    private String term;

    @Column(name="student_id")
    private Long studentId;

    // Constructor

    public WaitlistEnrollment(){}
    public WaitlistEnrollment(int waitlistPosition, Waitlist waitlist, User student) {
        this.waitlistPosition = waitlistPosition;
        this.waitlist = waitlist;
        this.student = student;
        this.term = waitlist.getTerm();
        this.studentId = student.getStudentId();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setWaitlistPosition(int waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }

    public Waitlist getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(Waitlist waitlist) {
        this.waitlist = waitlist;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
    
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}