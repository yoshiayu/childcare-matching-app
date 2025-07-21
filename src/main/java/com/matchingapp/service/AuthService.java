package com.matchingapp.service;

import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.User;
import com.matchingapp.repository.NurseryRepository;
import com.matchingapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final NurseryRepository nurseryRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, NurseryRepository nurseryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.nurseryRepository = nurseryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered as a nurse.");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encode password
        user.setRole("NURSE");
        return userRepository.save(user);
    }

    @Transactional
    public Nursery registerNursery(String name, String email, String password, String address, String philosophy) {
        if (nurseryRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered as a nursery.");
        }
        Nursery nursery = new Nursery();
        nursery.setName(name);
        nursery.setEmail(email);
        nursery.setPassword(passwordEncoder.encode(password)); // Encode password
        nursery.setAddress(address);
        nursery.setPhilosophy(philosophy);
        return nurseryRepository.save(nursery);
    }

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);
        // In a real application, you would send this token via email
        System.out.println("User Password Reset Token for " + email + ": " + token);
    }

    @Transactional
    public void createPasswordResetTokenForNursery(String email) {
        Nursery nursery = nurseryRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nursery not found with email: " + email));
        String token = UUID.randomUUID().toString();
        nursery.setResetToken(token);
        nursery.setResetTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        nurseryRepository.save(nursery);
        // In a real application, you would send this token via email
        System.out.println("Nursery Password Reset Token for " + email + ": " + token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Try to find in users (nurses)
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Password reset token has expired.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiryDate(null);
            userRepository.save(user);
            return;
        }

        // Try to find in nurseries
        Optional<Nursery> nurseryOptional = nurseryRepository.findByResetToken(token);
        if (nurseryOptional.isPresent()) {
            Nursery nursery = nurseryOptional.get();
            if (nursery.getResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Password reset token has expired.");
            }
            nursery.setPassword(passwordEncoder.encode(newPassword));
            nursery.setResetToken(null);
            nursery.setResetTokenExpiryDate(null);
            nurseryRepository.save(nursery);
            return;
        }

        throw new IllegalArgumentException("Invalid password reset token.");
    }
}