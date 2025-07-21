package com.matchingapp.service;

import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.User;
import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.Interview;
import com.matchingapp.repository.JobPostingRepository;
import com.matchingapp.repository.UserRepository;
import com.matchingapp.repository.NurseryRepository;
import com.matchingapp.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final NurseryRepository nurseryRepository;
    private final JobPostingRepository jobPostingRepository;
    private final InterviewRepository interviewRepository;

    public AdminService(UserRepository userRepository, NurseryRepository nurseryRepository, JobPostingRepository jobPostingRepository, InterviewRepository interviewRepository) {
        this.userRepository = userRepository;
        this.nurseryRepository = nurseryRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.interviewRepository = interviewRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Nursery> getAllNurseries() {
        return nurseryRepository.findAll();
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll();
    }

    @Transactional
    public void approveJobPosting(Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        jobPosting.setStatus("公開");
        jobPostingRepository.save(jobPosting);
    }

    @Transactional
    public void rejectJobPosting(Long jobPostingId, String reason) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        jobPosting.setStatus("却下");
        jobPosting.setDescription(jobPosting.getDescription() + "\n\n却下理由: " + reason); // Add rejection reason to description
        jobPostingRepository.save(jobPosting);
    }

    @Transactional
    public void requestJobPostingCorrection(Long jobPostingId, String reason) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        jobPosting.setStatus("修正依頼");
        jobPosting.setDescription(jobPosting.getDescription() + "\n\n修正依頼: " + reason); // Add correction reason to description
        jobPostingRepository.save(jobPosting);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Assuming 'enabled' field for user status, if not, add it to User entity
        // For now, let's just toggle a conceptual status or role
        // This needs to be properly implemented based on actual User entity fields
        // user.setRole(user.getRole().equals("DISABLED") ? "NURSE" : "DISABLED");
        // userRepository.save(user);
    }

    @Transactional
    public void toggleNurseryStatus(Long nurseryId) {
        Nursery nursery = nurseryRepository.findById(nurseryId)
                .orElseThrow(() -> new IllegalArgumentException("Nursery not found"));
        // Similar to user status, needs proper implementation
        // nursery.setStatus(nursery.getStatus().equals("DISABLED") ? "ACTIVE" : "DISABLED");
        // nurseryRepository.save(nursery);
    }
}