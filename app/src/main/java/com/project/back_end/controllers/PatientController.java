package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.CommonService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final CommonService service;

    // Constructor Injection
    public PatientController(
            PatientService patientService,
            CommonService service
    ) {
        this.patientService = patientService;
        this.service = service;
    }

    // -------------------------------------------------
    // 1. GET PATIENT DETAILS
    // -------------------------------------------------
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(
            @PathVariable String token
    ) {

        // Validate token for patient role
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return patientService.getPatientDetails(token);
    }

    // -------------------------------------------------
    // 2. CREATE NEW PATIENT (SIGNUP)
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(
            @RequestBody Patient patient
    ) {

        // Check if patient already exists
        boolean valid = service.validatePatient(patient);

        if (!valid) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error",
                            "Patient with email id or phone no already exist"
                    ));
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }

    // -------------------------------------------------
    // 3. PATIENT LOGIN
    // -------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Login login
    ) {
        return service.validatePatientLogin(login);
    }

    // -------------------------------------------------
    // 4. GET PATIENT APPOINTMENTS
    // -------------------------------------------------
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return patientService.getPatientAppointment(id, token);
    }

    // -------------------------------------------------
    // 5. FILTER PATIENT APPOINTMENTS
    // -------------------------------------------------
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return service.filterPatient(condition, name, token);
    }
}
