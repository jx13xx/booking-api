package com.booking.api.repository;

import com.booking.api.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientNameAndPatientPhone(String patientName, String patientPhone);
}

