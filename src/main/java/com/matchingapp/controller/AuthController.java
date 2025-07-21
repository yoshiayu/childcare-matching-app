package com.matchingapp.controller;

import com.matchingapp.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register/nurse")
    public String showNurseRegisterForm() {
        return "register_nurse";
    }

    @PostMapping("/register/nurse")
    public String registerNurse(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerUser(name, email, password);
            redirectAttributes.addFlashAttribute("successMessage", "保育士アカウントの登録が完了しました！ログインしてください。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register/nurse";
        }
    }

    @GetMapping("/register/nursery")
    public String showNurseryRegisterForm() {
        return "register_nursery";
    }

    @PostMapping("/register/nursery")
    public String registerNursery(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("address") String address,
            @RequestParam("philosophy") String philosophy,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerNursery(name, email, password, address, philosophy);
            redirectAttributes.addFlashAttribute("successMessage", "保育園アカウントの登録が完了しました！ログインしてください。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register/nursery";
        }
    }

    // @GetMapping("/dashboard")
    // public String dashboard() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     if (authentication != null && authentication.isAuthenticated()) {
    //         if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NURSE"))) {
    //             return "dashboard";
    //         } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NURSERY"))) {
    //             return "dashboard";
    //         } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
    //             return "redirect:/admin/dashboard";
    //         }
    //     }
    //     return "redirect:/login";
    // }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordRequest(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            // Determine if it's a user or nursery email
            if (email.contains("@example.com")) { // Simple heuristic for demo
                authService.createPasswordResetTokenForUser(email);
            } else {
                authService.createPasswordResetTokenForNursery(email);
            }
            redirectAttributes.addFlashAttribute("successMessage", "パスワード再設定の案内をメールで送信しました。ご確認ください。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate token existence and expiry (handled in service)
            // Just pass the token to the form
            model.addAttribute("token", token);
            return "reset_password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "無効なトークンまたは期限切れのトークンです。");
            return "redirect:/login";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {
        try {
            authService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "パスワードが正常にリセットされました。新しいパスワードでログインしてください。");
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reset-password?token=" + token; // Stay on reset page with error
        }
    }
}