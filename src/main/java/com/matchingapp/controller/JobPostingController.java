package com.matchingapp.controller;

import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.User;
import com.matchingapp.service.JobPostingService;
import com.matchingapp.service.NurseryService;
import com.matchingapp.service.FavoriteService; // Add this import
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/job-postings")
public class JobPostingController {

    private final JobPostingService jobPostingService;
    private final NurseryService nurseryService;
    private final FavoriteService favoriteService; // Declare FavoriteService

    public JobPostingController(JobPostingService jobPostingService, NurseryService nurseryService, FavoriteService favoriteService) {
        this.jobPostingService = jobPostingService;
        this.nurseryService = nurseryService;
        this.favoriteService = favoriteService; // Initialize FavoriteService
    }

    @GetMapping
    public String listJobPostings(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "workTime", required = false) String workTime,
            Model model) {
        // Use the primary search method with default pagination and sorting
        Page<JobPosting> jobPostingsPage = jobPostingService.searchJobPostings(null, area, salaryMin, null, "createdAt", "desc", 0, 100);
        model.addAttribute("jobPostings", jobPostingsPage.getContent());
        return "job_posting_list";
    }

    @GetMapping("/{id}")
    public String showJobPostingDetail(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        model.addAttribute("jobPosting", jobPosting);

        if (userDetails != null) {
            User currentUser = (User) userDetails; // Assuming UserDetails can be cast to User entity
            model.addAttribute("isFavorited", favoriteService.isFavorited(currentUser, id));
        }
        return "job_posting_detail";
    }
}