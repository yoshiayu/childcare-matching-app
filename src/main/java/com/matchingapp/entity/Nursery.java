package com.matchingapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nurseries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nursery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String philosophy;

    private Double latitude;
    private Double longitude;

    @Column(name = "reset_token") // New field
    private String resetToken;

    @Column(name = "reset_token_expiry_date") // New field
    private LocalDateTime resetTokenExpiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
