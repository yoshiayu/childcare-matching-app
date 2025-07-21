package com.matchingapp.service;

import com.matchingapp.entity.User;
import com.matchingapp.entity.Qualification;
import com.matchingapp.entity.UserQualification;
import com.matchingapp.entity.WorkExperience;
import com.matchingapp.repository.UserRepository;
import com.matchingapp.repository.QualificationRepository;
import com.matchingapp.repository.UserQualificationRepository;
import com.matchingapp.repository.WorkExperienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final QualificationRepository qualificationRepository;
    private final UserQualificationRepository userQualificationRepository;
    private final WorkExperienceRepository workExperienceRepository;

    public UserService(UserRepository userRepository, QualificationRepository qualificationRepository, UserQualificationRepository userQualificationRepository, WorkExperienceRepository workExperienceRepository) {
        this.userRepository = userRepository;
        this.qualificationRepository = qualificationRepository;
        this.userQualificationRepository = userQualificationRepository;
        this.workExperienceRepository = workExperienceRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User saveUserProfile(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> getAllNurses() {
        return userRepository.findByRole("NURSE");
    }

    public List<Qualification> getAllQualifications() {
        return qualificationRepository.findAll();
    }

    public List<UserQualification> getUserQualifications(User user) {
        return userQualificationRepository.findByUser(user);
    }

    @Transactional
    public UserQualification addUserQualification(User user, Long qualificationId) {
        Qualification qualification = qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new IllegalArgumentException("Qualification not found"));
        UserQualification userQualification = new UserQualification();
        userQualification.setUser(user);
        userQualification.setQualification(qualification);
        return userQualificationRepository.save(userQualification);
    }

    @Transactional
    public void removeUserQualification(Long userQualificationId) {
        userQualificationRepository.deleteById(userQualificationId);
    }

    public List<WorkExperience> getUserWorkExperiences(User user) {
        return workExperienceRepository.findByUser(user);
    }

    @Transactional
    public WorkExperience addWorkExperience(User user, String companyName, String jobTitle, LocalDate startDate, LocalDate endDate, String description) {
        WorkExperience workExperience = new WorkExperience();
        workExperience.setUser(user);
        workExperience.setCompanyName(companyName);
        workExperience.setJobTitle(jobTitle);
        workExperience.setStartDate(startDate);
        workExperience.setEndDate(endDate);
        workExperience.setDescription(description);
        return workExperienceRepository.save(workExperience);
    }

    @Transactional
    public void removeWorkExperience(Long workExperienceId) {
        workExperienceRepository.deleteById(workExperienceId);
    }
}