package com.matchingapp.controller;

import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.User;
import com.matchingapp.service.AdminService;
import com.matchingapp.service.JobPostingService;
import com.matchingapp.service.NurseryService;
import com.matchingapp.service.UserService;
import com.matchingapp.service.CsvExportService; // Add this import
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final NurseryService nurseryService;
    private final JobPostingService jobPostingService;
    private final CsvExportService csvExportService; // Declare CsvExportService

    public AdminController(AdminService adminService, UserService userService, NurseryService nurseryService, JobPostingService jobPostingService, CsvExportService csvExportService) {
        this.adminService = adminService;
        this.userService = userService;
        this.nurseryService = nurseryService;
        this.jobPostingService = jobPostingService;
        this.csvExportService = csvExportService; // Initialize CsvExportService
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Add some summary data for admin dashboard if needed
        model.addAttribute("totalUsers", adminService.getAllUsers().size());
        model.addAttribute("totalNurseries", adminService.getAllNurseries().size());
        model.addAttribute("pendingJobPostings", jobPostingService.searchJobPostings(null, null, null, null, null, null, 0, 10).getTotalElements()); // Use Page object
        return "admin_dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("nurseries", adminService.getAllNurseries());
        return "admin_user_management";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        adminService.toggleUserStatus(userId);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーのステータスを切り替えました。");
        return "redirect:/admin/users";
    }

    @PostMapping("/nurseries/{id}/toggle-status")
    public String toggleNurseryStatus(@PathVariable("id") Long nurseryId, RedirectAttributes redirectAttributes) {
        adminService.toggleNurseryStatus(nurseryId);
        redirectAttributes.addFlashAttribute("successMessage", "保育園のステータスを切り替えました。");
        return "redirect:/admin/users";
    }

    @GetMapping("/job-postings/review")
    public String reviewJobPostings(Model model) {
        model.addAttribute("jobPostings", jobPostingService.searchJobPostings(null, null, null, null, null, null, 0, 10).getContent().stream().filter(jp -> jp.getStatus().equals("審査中")).toList());
        return "admin_job_posting_review";
    }

    @PostMapping("/job-postings/{id}/approve")
    public String approveJobPosting(@PathVariable("id") Long jobPostingId, RedirectAttributes redirectAttributes) {
        adminService.approveJobPosting(jobPostingId);
        redirectAttributes.addFlashAttribute("successMessage", "求人票を承認しました。");
        return "redirect:/admin/job-postings/review";
    }

    @PostMapping("/job-postings/{id}/reject")
    public String rejectJobPosting(@PathVariable("id") Long jobPostingId, @RequestParam("reason") String reason, RedirectAttributes redirectAttributes) {
        adminService.rejectJobPosting(jobPostingId, reason);
        redirectAttributes.addFlashAttribute("successMessage", "求人票を却下しました。");
        return "redirect:/admin/job-postings/review";
    }

    @PostMapping("/job-postings/{id}/request-correction")
    public String requestJobPostingCorrection(@PathVariable("id") Long jobPostingId, @RequestParam("reason") String reason, RedirectAttributes redirectAttributes) {
        adminService.requestJobPostingCorrection(jobPostingId, reason);
        redirectAttributes.addFlashAttribute("successMessage", "求人票の修正を依頼しました。");
        return "redirect:/admin/job-postings/review";
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Placeholder for statistics data
        return "admin_statistics";
    }

    @GetMapping("/csv/users")
    public void exportUsersCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            csvExportService.writeUsersToCsv(writer, adminService.getAllUsers());
        }
    }

    @GetMapping("/csv/nurseries")
    public void exportNurseriesCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"nurseries.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            csvExportService.writeNurseriesToCsv(writer, adminService.getAllNurseries());
        }
    }

    @GetMapping("/csv/job-postings")
    public void exportJobPostingsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"job_postings.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            csvExportService.writeJobPostingsToCsv(writer, jobPostingService.getAllJobPostings());
        }
    }

    @GetMapping("/csv/interviews")
    public void exportInterviewsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"interviews.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            csvExportService.writeInterviewsToCsv(writer, adminService.getAllInterviews()); // Assuming getAllInterviews exists or will be added
        }
    }
}