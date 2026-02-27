package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // -------------------------------------------------
    // 1. Get Doctor Availability
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> docOpt = doctorRepository.findById(doctorId);
        if (docOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = docOpt.get();

        List<String> available = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end);

        for (Appointment appt : appointments) {
            String bookedTime = appt.getAppointmentTime()
                                    .toLocalTime()
                                    .toString();
            available.remove(bookedTime);
        }

        return available;
    }

    // -------------------------------------------------
    // 2. Save Doctor
    // -------------------------------------------------
    @Transactional
    public int saveDoctor(Doctor doctor) {

        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null)
                return -1; // already exists

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // -------------------------------------------------
    // 3. Update Doctor
    // -------------------------------------------------
    @Transactional
    public int updateDoctor(Doctor doctor) {

        Optional<Doctor> existing =
                doctorRepository.findById(doctor.getId());

        if (existing.isEmpty()) return -1;

        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // -------------------------------------------------
    // 4. Get All Doctors
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // -------------------------------------------------
    // 5. Delete Doctor
    // -------------------------------------------------
    @Transactional
    public int deleteDoctor(long id) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(id);

        if (doctorOpt.isEmpty()) return -1;

        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // -------------------------------------------------
    // 6. Validate Doctor Login
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null ||
            !doctor.getPassword().equals(login.getPassword())) {

            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        String token = tokenService.generateToken(doctor.getEmail());

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------
    // 7. Find Doctor By Name
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {

        Map<String, Object> result = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findByNameLike("%" + name + "%");

        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 8. Filter by Name + Specialty + Time
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name, specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 9. Filter by Name + Time
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(
            String name,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameLike("%" + name + "%");

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 10. Filter by Name + Specialty
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(
            String name,
            String specialty) {

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name, specialty);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 11. Filter by Specialty + Time
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(
            String specialty,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 12. Filter by Specialty
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specialty);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // 13. Filter by Time Only
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {

        List<Doctor> doctors = doctorRepository.findAll();

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    // -------------------------------------------------
    // PRIVATE: Time Filter Logic (AM / PM)
    // -------------------------------------------------
    private List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String amOrPm) {

        List<Doctor> filtered = new ArrayList<>();

        for (Doctor doctor : doctors) {

            for (String time : doctor.getAvailableTimes()) {

                int hour = Integer.parseInt(time.split(":")[0]);

                if ("AM".equalsIgnoreCase(amOrPm) && hour < 12) {
                    filtered.add(doctor);
                    break;
                }

                if ("PM".equalsIgnoreCase(amOrPm) && hour >= 12) {
                    filtered.add(doctor);
                    break;
                }
            }
        }

        return filtered;
    }
}