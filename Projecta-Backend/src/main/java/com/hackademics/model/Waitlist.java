package com.hackademics.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waitlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

    @Id
    @Column(name = "waitlist_id", length = 5)
    private String waitlistId;

    @Column(name = "course_id", length = 5, nullable = false)
    private String courseId;

    @Column(name = "waitlist_limit", nullable = false)
    private int waitlistLimit;
}