package com.matchingapp.controller;

import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.User;
import com.matchingapp.service.JobPostingService;
import com.matchingapp.service.NurseryService;
import com.matchingapp.service.InterviewService;
import com.matchingapp.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/nursery")
public class NurseryController {

    private final NurseryService nurseryService;
    private final JobPostingService jobPostingService;
    // private final InterviewService interviewService;
    private final UserService userService;

    public NurseryController(NurseryService nurseryService, JobPostingService jobPostingService, UserService userService) {
        this.nurseryService = nurseryService;
        this.jobPostingService = jobPostingService;
        // this.interviewService = interviewService;
        this.userService = userService;
    }

    @GetMapping("/my-page")
    public String showMyPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        model.addAttribute("nursery", currentNursery);
        return "nursery_my_page";
    }

    @GetMapping("/profile/edit")
    public String showProfileEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        model.addAttribute("nursery", currentNursery);
        return "nursery_profile_edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("philosophy") String philosophy,
            RedirectAttributes redirectAttributes) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));

        currentNursery.setName(name);
        currentNursery.setAddress(address);
        currentNursery.setPhilosophy(philosophy);

        nurseryService.saveNurseryProfile(currentNursery);
        redirectAttributes.addFlashAttribute("successMessage", "保育園情報を更新しました！");
        return "redirect:/nursery/my-page";
    }

    @GetMapping("/job-postings")
    public String manageJobPostings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        model.addAttribute("jobPostings", jobPostingService.getJobPostingsByNursery(currentNursery));
        return "nursery_job_posting_management";
    }

    @GetMapping("/job-postings/new")
    public String showCreateJobPostingForm(Model model) {
        model.addAttribute("jobPosting", new JobPosting());
        return "nursery_job_posting_form";
    }

    @PostMapping("/job-postings")
    public String createJobPosting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("area") String area,
            @RequestParam("salary") Integer salary,
            @RequestParam("workTime") String workTime,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        jobPostingService.createOrUpdateJobPosting(currentNursery.getId(), title, description, area, salary, workTime, status);
        redirectAttributes.addFlashAttribute("successMessage", "求人票を登録しました！");
        return "redirect:/nursery/job-postings";
    }

    @GetMapping("/job-postings/{id}/edit")
    public String showEditJobPostingForm(@PathVariable("id") Long id, Model model) {
        JobPosting jobPosting = jobPostingService.getJobPostingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        model.addAttribute("jobPosting", jobPosting);
        return "nursery_job_posting_form";
    }

    @PostMapping("/job-postings/{id}/update")
    public String updateJobPosting(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("area") String area,
            @RequestParam("salary") Integer salary,
            @RequestParam("workTime") String workTime,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        // Ensure the nursery owns this job posting
        JobPosting jobPosting = jobPostingService.getJobPostingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        if (!jobPosting.getNursery().getId().equals(currentNursery.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "この求人票は編集できません。");
            return "redirect:/nursery/job-postings";
        }
        jobPostingService.createOrUpdateJobPosting(currentNursery.getId(), title, description, area, salary, workTime, status);
        redirectAttributes.addFlashAttribute("successMessage", "求人票を更新しました！");
        return "redirect:/nursery/job-postings";
    }

    @PostMapping("/job-postings/{id}/toggle-status")
    public String toggleJobPostingStatus(@PathVariable("id") Long id, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        jobPostingService.updateJobPostingStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "求人票のステータスを更新しました。");
        return "redirect:/nursery/job-postings";
    }

    @GetMapping("/nurses")
    public String searchNurses(Model model) {
        List<User> nurses = userService.getAllNurses();
        model.addAttribute("nurses", nurses);
        return "nursery_nurse_search";
    }

    @GetMapping("/nurses/{id}")
    public String showNurseDetail(@PathVariable("id") Long id, Model model) {
        User nurse = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nurse not found"));
        model.addAttribute("nurse", nurse);
        return "nursery_nurse_detail";
    }

    @PostMapping("/interviews/request")
    public String requestInterviewFromNursery(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("userId") Long userId,
            @RequestParam("message") String message,
            @RequestParam("interviewDate") LocalDate interviewDate,
            RedirectAttributes redirectAttributes) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        try {
            // interviewService.createInterviewRequest(currentNursery.getId(), userId, message, interviewDate);
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を送信しました！");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nursery/nurses";
    }

    @GetMapping("/interviews")
    public String listNurseryInterviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Nursery currentNursery = nurseryService.getNurseryByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Nursery not found"));
        // model.addAttribute("interviews", interviewService.getInterviewsForNursery(currentNursery));
        return "nursery_interview_list";
    }

    @PostMapping("/interviews/{id}/accept")
    public String acceptInterviewByNursery(@PathVariable("id") Long interviewId, RedirectAttributes redirectAttributes) {
        try {
            // interviewService.updateInterviewStatus(interviewId, "承諾済");
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を承諾しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nursery/interviews";
    }

    @PostMapping("/interviews/{id}/decline")
    public String declineInterviewByNursery(@PathVariable("id") Long interviewId, RedirectAttributes redirectAttributes) {
        try {
            // interviewService.updateInterviewStatus(interviewId, "辞退済");
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を辞退しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nursery/interviews";
    }
}
