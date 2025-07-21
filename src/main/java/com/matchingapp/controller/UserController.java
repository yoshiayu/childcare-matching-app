package com.matchingapp.controller;

import com.matchingapp.entity.User;
import com.matchingapp.entity.Qualification;
import com.matchingapp.entity.WorkExperience;
import com.matchingapp.service.JobPostingService;
import com.matchingapp.service.UserService;
import com.matchingapp.service.InterviewService;
import com.matchingapp.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import com.matchingapp.entity.UserQualification;

@Controller
@RequestMapping("/nurse")
public class UserController {

    private final UserService userService;
    private final JobPostingService jobPostingService;
    private final InterviewService interviewService;
    private final FavoriteService favoriteService;

    public UserController(UserService userService, JobPostingService jobPostingService, InterviewService interviewService, FavoriteService favoriteService) {
        this.userService = userService;
        this.jobPostingService = jobPostingService;
        this.interviewService = interviewService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/my-page")
    public String showMyPage(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ログイン情報が取得できませんでした。再度ログインしてください。");
            return "redirect:/dashboard";
        }
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElse(null);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザー情報が見つかりません。再度ログインしてください。");
            return "redirect:/dashboard";
        }
        model.addAttribute("user", currentUser);
        List<UserQualification> qualifications = userService.getUserQualifications(currentUser);
        model.addAttribute("userQualifications", qualifications != null ? qualifications : Collections.emptyList());
        List<WorkExperience> workExperiences = userService.getUserWorkExperiences(currentUser);
        model.addAttribute("workExperiences", workExperiences != null ? workExperiences : Collections.emptyList());
        model.addAttribute("favoriteJobPostings", favoriteService.getFavoriteJobPostingsByUser(currentUser));
        model.addAttribute("interviews", interviewService.getInterviewsForNurse(currentUser));
        return "nurse_my_page";
    }

    @GetMapping("/profile/edit")
    public String showProfileEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", currentUser);
        model.addAttribute("allQualifications", userService.getAllQualifications());
        model.addAttribute("userQualifications", userService.getUserQualifications(currentUser));
        model.addAttribute("workExperiences", userService.getUserWorkExperiences(currentUser));
        return "nurse_profile_edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("desiredArea") String desiredArea,
            @RequestParam("desiredSalary") Integer desiredSalary,
            @RequestParam("desiredWorkTime") String desiredWorkTime,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setName(name);
        currentUser.setDesiredArea(desiredArea);
        currentUser.setDesiredSalary(desiredSalary);
        currentUser.setDesiredWorkTime(desiredWorkTime);

        userService.saveUserProfile(currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました！");
        return "redirect:/nurse/my-page";
    }

    @PostMapping("/profile/add-qualification")
    public String addQualification(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("qualificationId") Long qualificationId, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            userService.addUserQualification(currentUser, qualificationId);
            redirectAttributes.addFlashAttribute("successMessage", "資格を追加しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/profile/edit";
    }

    @PostMapping("/profile/remove-qualification/{id}")
    public String removeQualification(@PathVariable("id") Long userQualificationId, RedirectAttributes redirectAttributes) {
        userService.removeUserQualification(userQualificationId);
        redirectAttributes.addFlashAttribute("successMessage", "資格を削除しました。");
        return "redirect:/nurse/profile/edit";
    }

    @PostMapping("/profile/add-work-experience")
    public String addWorkExperience(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("companyName") String companyName,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.addWorkExperience(currentUser, companyName, jobTitle, startDate, endDate, description);
        redirectAttributes.addFlashAttribute("successMessage", "職務経歴を追加しました。");
        return "redirect:/nurse/profile/edit";
    }

    @PostMapping("/profile/remove-work-experience/{id}")
    public String removeWorkExperience(@PathVariable("id") Long workExperienceId, RedirectAttributes redirectAttributes) {
        userService.removeWorkExperience(workExperienceId);
        redirectAttributes.addFlashAttribute("successMessage", "職務経歴を削除しました。");
        return "redirect:/nurse/profile/edit";
    }

    @GetMapping("/job-postings")
    public String searchJobPostings(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "salaryMin", required = false) Integer salaryMin,
            @RequestParam(value = "philosophyKeyword", required = false) String philosophyKeyword,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            Page<com.matchingapp.entity.JobPosting> jobPostings = jobPostingService.searchJobPostings(keyword, area, salaryMin, philosophyKeyword, sortBy, sortOrder, page, size);
            model.addAttribute("jobPostings", jobPostings);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "求人情報の取得中にエラーが発生しました。");
            return "redirect:/dashboard";
        }
        return "nurse_job_search";
    }

    @GetMapping("/job-postings/{id}")
    public String showJobPostingDetail(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        com.matchingapp.entity.JobPosting jobPosting = jobPostingService.getJobPostingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));

        if (jobPosting.getNursery() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "この求人票の保育園情報が見つかりません。");
            return "redirect:/nurse/job-postings";
        }

        model.addAttribute("jobPosting", jobPosting);

        if (userDetails != null) {
            User currentUser = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("isFavorited", favoriteService.isFavorited(currentUser, id));
        }
        return "nurse_job_detail";
    }

    @PostMapping("/job-postings/{id}/favorite")
    public String addFavoriteJobPosting(@PathVariable("id") Long jobPostingId, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            favoriteService.addFavorite(currentUser, jobPostingId);
            redirectAttributes.addFlashAttribute("successMessage", "求人をお気に入りに追加しました！");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/job-postings/{id}";
    }

    @PostMapping("/job-postings/{id}/unfavorite")
    public String removeFavoriteJobPosting(@PathVariable("id") Long jobPostingId, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            favoriteService.removeFavorite(currentUser, jobPostingId);
            redirectAttributes.addFlashAttribute("successMessage", "求人をお気に入りから削除しました。");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/job-postings/{id}";
    }

    @GetMapping("/favorites")
    public String listFavoriteJobPostings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("favoriteJobPostings", favoriteService.getFavoriteJobPostingsByUser(currentUser));
        return "nurse_favorites";
    }

    @PostMapping("/interviews/request")
    public String requestInterview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("nurseryId") Long nurseryId,
            @RequestParam("message") String message,
            @RequestParam("interviewDate") LocalDate interviewDate,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            interviewService.createInterviewRequest(nurseryId, currentUser.getId(), message, interviewDate);
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を送信しました！");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/job-postings";
    }

    @GetMapping("/interviews")
    public String listMyInterviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("interviews", interviewService.getInterviewsForNurse(currentUser));
        return "nurse_interview_list";
    }

    @PostMapping("/interviews/{id}/accept")
    public String acceptInterview(@PathVariable("id") Long interviewId, RedirectAttributes redirectAttributes) {
        try {
            interviewService.updateInterviewStatus(interviewId, "承諾済");
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を承諾しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/interviews";
    }

    @PostMapping("/interviews/{id}/decline")
    public String declineInterview(@PathVariable("id") Long interviewId, RedirectAttributes redirectAttributes) {
        try {
            interviewService.updateInterviewStatus(interviewId, "辞退済");
            redirectAttributes.addFlashAttribute("successMessage", "面談依頼を辞退しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nurse/interviews";
    }
}