package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.CommonService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

   private final CommonService service;

    // Constructor Injection
    public AdminController(CommonService service) {
        this.service = service;
    }

    // -------------------------------------------------
    // Admin Login Endpoint
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(
            @RequestBody Admin admin
    ) {
        return service.validateAdmin(admin);
    }
}