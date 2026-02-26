package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.Service;

import java.util.Map;

@Controller
public class DashboardController {

    // 🔹 Inject the shared Service that validates tokens
    @Autowired
    private Service service;


    // =========================
    // 👨‍💼 ADMIN DASHBOARD
    // =========================
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        // Validate token for admin role
        Map<String, Object> result = service.validateToken(token, "admin");

        // If result is empty → token valid
        if (result.isEmpty()) {
            return "admin/adminDashboard";   // Thymeleaf template path
        }

        // Invalid token → redirect to login page
        return "redirect:/";
    }


    // =========================
    // 👨‍⚕️ DOCTOR DASHBOARD
    // =========================
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        // Validate token for doctor role
        Map<String, Object> result = service.validateToken(token, "doctor");

        // If valid → show dashboard
        if (result.isEmpty()) {
            return "doctor/doctorDashboard";
        }

        // Invalid → redirect to home/login
        return "redirect:/";
    }
}
