package com.project.back_end.controllers;

import com.project.back_end.model.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.CommonService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CommonService service;

    // Constructor Injection
    public AppointmentController(
            AppointmentService appointmentService,
            CommonService service
    ) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // -------------------------------------------------
    // 1. GET APPOINTMENTS (Doctor only)
    // -------------------------------------------------
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {

        // Validate Doctor Token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        LocalDate selectedDate = LocalDate.parse(date);

        Map<String, Object> result =
                appointmentService.getAppointment(patientName, selectedDate, token);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------
    // 2. BOOK APPOINTMENT (Patient only)
    // -------------------------------------------------
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {

        // Validate Patient Token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        // Validate Appointment Slot
        int check = service.validateAppointment(appointment);

        if (check == -1) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Doctor not found"));
        }

        if (check == 0) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Selected time slot unavailable"));
        }

        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Appointment booked successfully"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to book appointment"));
    }

    // -------------------------------------------------
    // 3. UPDATE APPOINTMENT (Patient only)
    // -------------------------------------------------
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.updateAppointment(appointment);
    }

    // -------------------------------------------------
    // 4. CANCEL APPOINTMENT (Patient only)
    // -------------------------------------------------
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}