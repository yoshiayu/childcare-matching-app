package com.matchingapp.repository;

import com.matchingapp.entity.Nursery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NurseryRepository extends JpaRepository<Nursery, Long> {
    Optional<Nursery> findByEmail(String email);
    Optional<Nursery> findByResetToken(String resetToken); // Add this line
}