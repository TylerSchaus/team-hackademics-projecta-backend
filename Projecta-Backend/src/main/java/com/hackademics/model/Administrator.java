package com.hackademics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Administrator {
    
    @Id
    @Column(nullable=false, unique= true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId; 

    // Getters and Setters 

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

}
