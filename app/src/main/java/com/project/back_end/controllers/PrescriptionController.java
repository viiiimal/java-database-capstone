package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.CommonService;
import com.project.back_end.services.AppointmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final CommonService service;
    private final AppointmentService appointmentService;

    // Constructor Injection
    public PrescriptionController(
            PrescriptionService prescriptionService,
            CommonService service,
            AppointmentService appointmentService
    ) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // -------------------------------------------------
    // 1. SAVE PRESCRIPTION (Doctor only)
    // -------------------------------------------------
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {

        // Validate doctor token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        // Optional: Mark appointment as completed
        if (prescription.getAppointmentId() != null) {
            appointmentService.changeStatus(
                    prescription.getAppointmentId(),
                    1   // status = completed
            );
        }

        return prescriptionService.savePrescription(prescription);
    }

    // -------------------------------------------------
    // 2. GET PRESCRIPTION BY APPOINTMENT ID
    // -------------------------------------------------
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {

        // Validate doctor token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity
                    .status(validation.getStatusCode())
                    .body(Map.of(
                            "error",
                            validation.getBody().get("error")
                    ));
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}