package com.booking.api.service.PatientService;

import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PatientServiceAPI {

    ResponseEntity<PatientResponseDTO> createPatient(PatientDTO patientDTO);

    ResponseEntity<PatientResponseDTO> retrievePatient(String id);

    ResponseEntity<PatientResponseDTO> deletePatient(String id);
}
