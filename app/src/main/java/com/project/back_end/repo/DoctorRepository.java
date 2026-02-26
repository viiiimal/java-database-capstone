package com.project.back_end.repo;

import com.project.back_end.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1️⃣ Find doctor by email
    Doctor findByEmail(String email);


    // 2️⃣ Find doctors by partial name match
    @Query("""
        SELECT d FROM Doctor d
        WHERE d.name LIKE CONCAT('%', :name, '%')
    """)
    List<Doctor> findByNameLike(String name);


    // 3️⃣ Filter by partial name + exact specialty (case-insensitive)
    @Query("""
        SELECT d FROM Doctor d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
        AND LOWER(d.specialty) = LOWER(:specialty)
    """)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            String name,
            String specialty
    );


    // 4️⃣ Find by specialty ignoring case
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

}