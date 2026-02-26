package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // -------------------------------------------------
    // 1. Create Patient
    // -------------------------------------------------
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // -------------------------------------------------
    // 2. Get Patient Appointments
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            Long id,
            String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);

        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(id);

        List<AppointmentDTO> dtoList = convertToDTO(appointments);

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 3. Filter By Condition (Past / Future)
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(
            String condition,
            Long id) {

        Map<String, Object> response = new HashMap<>();

        int status;

        if ("past".equalsIgnoreCase(condition)) status = 1;
        else if ("future".equalsIgnoreCase(condition)) status = 0;
        else {
            response.put("message", "Invalid condition");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                id, status);

        response.put("appointments", convertToDTO(appointments));

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 4. Filter By Doctor Name
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(
            String name,
            Long patientId) {

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository
                        .filterByDoctorNameAndPatientId(name, patientId);

        response.put("appointments", convertToDTO(appointments));

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 5. Filter By Doctor + Condition
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>>
    filterByDoctorAndCondition(
            String condition,
            String name,
            long patientId) {

        Map<String, Object> response = new HashMap<>();

        int status;

        if ("past".equalsIgnoreCase(condition)) status = 1;
        else if ("future".equalsIgnoreCase(condition)) status = 0;
        else {
            response.put("message", "Invalid condition");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository
                        .filterByDoctorNameAndPatientIdAndStatus(
                                name, patientId, status);

        response.put("appointments", convertToDTO(appointments));

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 6. Get Patient Details
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(
            String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);

        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(404).body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // Helper: Convert Appointment → DTO
    // -------------------------------------------------
    private List<AppointmentDTO> convertToDTO(
            List<Appointment> appointments) {

        List<AppointmentDTO> dtoList = new ArrayList<>();

        for (Appointment appt : appointments) {

            AppointmentDTO dto = new AppointmentDTO(
                    appt.getId(),
                    appt.getDoctor().getId(),
                    appt.getDoctor().getName(),
                    appt.getPatient().getId(),
                    appt.getPatient().getName(),
                    appt.getPatient().getEmail(),
                    appt.getPatient().getPhone(),
                    appt.getPatient().getAddress(),
                    appt.getAppointmentTime(),
                    appt.getStatus()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }
}