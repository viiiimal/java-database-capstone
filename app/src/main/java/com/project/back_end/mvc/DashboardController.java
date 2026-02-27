package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.CommonService;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private CommonService service;

    // =========================
    // 👨‍💼 ADMIN DASHBOARD
    // =========================
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        ResponseEntity<Map<String, String>> result =
                service.validateToken(token, "admin");

        if (result.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }

        return "redirect:/";
    }


    // =========================
    // 👨‍⚕️ DOCTOR DASHBOARD
    // =========================
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        ResponseEntity<Map<String, String>> result =
                service.validateToken(token, "doctor");

        if (result.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }

        return "redirect:/";
    }
}