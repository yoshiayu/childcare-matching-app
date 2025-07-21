package com.matchingapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // e.g., NURSE, NURSERY, ADMIN

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "desired_area")
    private String desiredArea;

    @Column(name = "desired_salary")
    private Integer desiredSalary;

    @Column(name = "desired_work_time")
    private String desiredWorkTime;

    @Column(name = "reset_token") // New field
    private String resetToken;

    @Column(name = "reset_token_expiry_date") // New field
    private LocalDateTime resetTokenExpiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}