package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.CommonService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final CommonService service;

    // Constructor Injection
    public DoctorController(
            DoctorService doctorService,
            CommonService service
    ) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // -------------------------------------------------
    // 1. GET DOCTOR AVAILABILITY
    // -------------------------------------------------
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {

        // Validate token for provided role
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        LocalDate selectedDate = LocalDate.parse(date);

        List<String> availability =
                doctorService.getDoctorAvailability(doctorId, selectedDate);

        return ResponseEntity.ok(
                Map.of("availability", availability)
        );
    }

    // -------------------------------------------------
    // 2. GET ALL DOCTORS
    // -------------------------------------------------
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {

        List<Doctor> doctors = doctorService.getDoctors();

        return ResponseEntity.ok(
                Map.of("doctors", doctors)
        );
    }

    // -------------------------------------------------
    // 3. ADD NEW DOCTOR (Admin only)
    // -------------------------------------------------
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Doctor added to db"));
        }

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Doctor already exists"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Some internal error occurred"));
    }

    // -------------------------------------------------
    // 4. DOCTOR LOGIN
    // -------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @RequestBody Login login
    ) {
        return doctorService.validateDoctor(login);
    }

    // -------------------------------------------------
    // 5. UPDATE DOCTOR (Admin only)
    // -------------------------------------------------
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.ok(
                    Map.of("message", "Doctor updated")
            );
        }

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor not found"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Some internal error occurred"));
    }

    // -------------------------------------------------
    // 6. DELETE DOCTOR (Admin only)
    // -------------------------------------------------
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            return ResponseEntity.ok(
                    Map.of("message", "Doctor deleted successfully")
            );
        }

        if (result == -1) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor not found with id"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Some internal error occurred"));
    }

    // -------------------------------------------------
    // 7. FILTER DOCTORS
    // -------------------------------------------------
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {

        Map<String, Object> result =
                service.filterDoctor(name, speciality, time);

        return ResponseEntity.ok(result);
    }
}
