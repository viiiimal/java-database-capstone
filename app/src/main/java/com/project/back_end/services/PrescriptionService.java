package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // -------------------------------------------------
    // 1. Save Prescription
    // -------------------------------------------------
    public ResponseEntity<Map<String, String>> savePrescription(
            Prescription prescription) {

        Map<String, String> response = new HashMap<>();

        try {

            // Check if prescription already exists for appointment
            List<Prescription> existing =
                    prescriptionRepository
                            .findByAppointmentId(
                                    prescription.getAppointmentId());

            if (!existing.isEmpty()) {
                response.put("message",
                        "Prescription already exists for this appointment");
                return ResponseEntity.badRequest().body(response);
            }

            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message",
                    "Failed to save prescription");
            return ResponseEntity.status(500).body(response);
        }
    }

    // -------------------------------------------------
    // 2. Get Prescription by Appointment ID
    // -------------------------------------------------
    public ResponseEntity<Map<String, Object>> getPrescription(
            Long appointmentId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Prescription> prescriptions =
                    prescriptionRepository
                            .findByAppointmentId(appointmentId);

            if (prescriptions.isEmpty()) {
                response.put("message",
                        "No prescription found for this appointment");
                return ResponseEntity.ok(response);
            }

            response.put("prescriptions", prescriptions);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message",
                    "Error retrieving prescription");
            return ResponseEntity.status(500).body(response);
        }
    }
}