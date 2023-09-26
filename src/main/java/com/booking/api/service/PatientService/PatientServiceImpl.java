package com.booking.api.service.PatientService;

import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import com.booking.api.model.Patient;
import com.booking.api.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.booking.api.constants.Constants.*;

@Service
public class PatientServiceImpl implements PatientServiceAPI {

    private final ModelMapper modelMapper;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(ModelMapper modelMapper, PatientRepository patientRepository) {
        this.modelMapper = modelMapper;
        this.patientRepository = patientRepository;
    }

    @Override
    public ResponseEntity<PatientResponseDTO> createPatient(PatientDTO patientDTO) {
        Patient patient = mapPatientDTOToEntity(patientDTO);

        Optional<Patient> existingPatient = getPatient(patientDTO);

        if(existingPatient.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(buildResponseDTO(PATIENT_ALREADY_EXITS));
        }
        Patient savedPatient = savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponseDTO(savedPatient, PATIENT_CREATED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<PatientResponseDTO> retrievePatient(String id) {
        try {
            Long patientId = Long.parseLong(id);
            Optional<Patient> patient = patientRepository.findById(patientId);
            return patient.map(p -> ResponseEntity.ok(buildResponseDTO(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponseDTO(PATIENT_NOT_FOUND)));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(buildResponseDTO("Invalid patient ID format"));
        }

    }

    private Optional<Patient> getPatient(PatientDTO patientDTO) {
        Optional<Patient> existingPatient = patientRepository.findByPatientNameAndPatientPhone(
            patientDTO.getName(),
            patientDTO.getPhone()
        );
        return existingPatient;
    }

    private Patient mapPatientDTOToEntity(PatientDTO patientDTO) {
        return modelMapper.map(patientDTO, Patient.class);
    }

    private Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    private PatientResponseDTO buildResponseDTO(Patient patient, String message) {
        return PatientResponseDTO.builder().withMessage(message).withId(patient.getPatientID().toString()).build();
    }

    private PatientResponseDTO buildResponseDTO(Patient patient) {
        return PatientResponseDTO.builder().withPatient(patient).withMessage(PATIENT_RETRIEVED).withStatus(200).build();
    }

    private PatientResponseDTO buildResponseDTO(String message) {
        if(message.equals(PATIENT_ALREADY_EXITS)){
            return PatientResponseDTO.builder().withMessage(message).withStatus(409).build();
        }
        if(message.equals(PATIENT_NOT_FOUND)){
            return PatientResponseDTO.builder().withMessage(message).withStatus(404).build();
        }
        return PatientResponseDTO.builder().withMessage(message).build();
    }
}
