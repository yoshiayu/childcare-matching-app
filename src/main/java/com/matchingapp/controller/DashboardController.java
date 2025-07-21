package com.matchingapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String redirectToMyPage(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getAuthorities() == null) {
            return "redirect:/login?error";
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NURSERY"))) {
            return "redirect:/nursery/my-page";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NURSE"))) {
            // NURSEはこちらの汎用ダッシュボードに遷移
            return "dashboard";
        }

        return "redirect:/login?error=unauthorized";
    }
} 