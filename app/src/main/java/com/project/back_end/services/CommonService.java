package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;

import com.project.back_end.repo.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommonService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Constructor Injection
    public CommonService(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService
    ) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // -------------------------------------------------
    // 1. Validate Token
    // -------------------------------------------------
    public ResponseEntity<Map<String, String>> validateToken(
            String token,
            String user
    ) {

        Map<String, String> response = new HashMap<>();

        boolean valid = tokenService.validateToken(token, user);

        if (!valid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 2. Validate Admin Login
    // -------------------------------------------------
    public ResponseEntity<Map<String, String>> validateAdmin(
            Admin receivedAdmin
    ) {

        Map<String, String> response = new HashMap<>();

        try {
            Admin admin =
                    adminRepository
                            .findByUsername(receivedAdmin.getUsername());

            if (admin == null ||
                !admin.getPassword()
                        .equals(receivedAdmin.getPassword())) {

                response.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(response);
            }

            String token =
                    tokenService.generateToken(admin.getUsername());

            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // -------------------------------------------------
    // 3. Filter Doctors
    // -------------------------------------------------
    public Map<String, Object> filterDoctor(
            String name,
            String specialty,
            String time
    ) {

        if (name != null && specialty != null && time != null)
            return doctorService
                    .filterDoctorsByNameSpecilityandTime(
                            name, specialty, time);

        if (name != null && time != null)
            return doctorService
                    .filterDoctorByNameAndTime(name, time);

        if (name != null && specialty != null)
            return doctorService
                    .filterDoctorByNameAndSpecility(name, specialty);

        if (specialty != null && time != null)
            return doctorService
                    .filterDoctorByTimeAndSpecility(specialty, time);

        if (name != null)
            return doctorService.findDoctorByName(name);

        if (specialty != null)
            return doctorService.filterDoctorBySpecility(specialty);

        if (time != null)
            return doctorService.filterDoctorsByTime(time);

        // No filters → return all doctors
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorService.getDoctors());
        return result;
    }

    // -------------------------------------------------
    // 4. Validate Appointment
    // -------------------------------------------------
    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt =
                doctorRepository.findById(
                        appointment.getDoctor().getId());

        if (doctorOpt.isEmpty())
            return -1;

        Doctor doctor = doctorOpt.get();

        List<String> availableSlots =
                doctorService.getDoctorAvailability(
                        doctor.getId(),
                        appointment.getAppointmentTime()
                                .toLocalDate()
                );

        String requestedTime =
                appointment.getAppointmentTime()
                        .toLocalTime()
                        .toString();

        return availableSlots.contains(requestedTime) ? 1 : 0;
    }

    // -------------------------------------------------
    // 5. Validate Patient Registration
    // -------------------------------------------------
    public boolean validatePatient(Patient patient) {

        Patient existing =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone()
                );

        return existing == null;
    }

    // -------------------------------------------------
    // 6. Validate Patient Login
    // -------------------------------------------------
    public ResponseEntity<Map<String, String>> validatePatientLogin(
            Login login
    ) {

        Map<String, String> response = new HashMap<>();

        Patient patient =
                patientRepository.findByEmail(login.getIdentifier());

        if (patient == null ||
            !patient.getPassword().equals(login.getPassword())) {

            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        String token =
                tokenService.generateToken(
                        patient.getEmail() );

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 7. Filter Patient Appointments
    // -------------------------------------------------
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {

        String email = tokenService.extractEmail(token);

        Patient patient =
                patientRepository.findByEmail(email);

        if (patient == null)
            return ResponseEntity.status(401).build();

        Long id = patient.getId();

        if (condition != null && name != null)
            return patientService
                    .filterByDoctorAndCondition(
                            condition,
                            name,
                            id
                    );

        if (condition != null)
            return patientService
                    .filterByCondition(condition, id);

        if (name != null)
            return patientService
                    .filterByDoctor(name, id);

        return patientService.getPatientAppointment(id, token);
    }
}