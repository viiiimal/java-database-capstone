package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    // -----------------------------
    // 1. Book Appointment
    // -----------------------------
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // -----------------------------
    // 2. Update Appointment
    // -----------------------------
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt =
                appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        // Update fields
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(appointment.getDoctor());

        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // 3. Cancel Appointment
    // -----------------------------
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt =
                appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Validate token owner (patient)
        Long patientId = tokenService.extractId(token);

        if (!appointment.getPatient().getId().equals(patientId)) {
            response.put("message", "Unauthorized cancellation attempt");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);

        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // 4. Get Appointments for Doctor
    // -----------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(
            String pname,
            LocalDate date,
            String token) {

        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.extractId(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments;

        if (pname == null || pname.equalsIgnoreCase("null")) {

            appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctorId, start, end);

        } else {

            appointments =
                    appointmentRepository
                            .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                    doctorId, pname, start, end);
        }

        result.put("appointments", appointments);
        return result;
    }

}