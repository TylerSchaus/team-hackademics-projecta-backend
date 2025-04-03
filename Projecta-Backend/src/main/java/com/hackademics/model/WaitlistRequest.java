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
@Table(name = "waitlist_requests")
public class WaitlistRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waitlist_id", nullable =false)
    private Waitlist waitlist; 

    @ManyToOne
    @JoinColumn(name = "student_reference", nullable = false)
    private User student;

    @Column(name="student_id")
    private Long studentId;

    // Constructor

    public WaitlistRequest(){}
    public WaitlistRequest(Waitlist waitlist, User student) {
        this.waitlist = waitlist;
        this.student = student;
        this.studentId = student.getStudentId();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}