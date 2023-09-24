package com.booking.api.service;

import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import com.booking.api.model.Patient;
import com.booking.api.repository.PatientRepository;
import com.booking.api.service.PatientService.PatientServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PatientServiceImplTest {
    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PatientRepository patientRepository;

    @Test
    public void testCreatePatient_Success() {
        // Create a test patientDTO
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("Test Patient");
        patientDTO.setPhone("1234567890");

        // Mock the behavior of patientRepository
        when(patientRepository.findByPatientNameAndPatientPhone(anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class)))
            .thenAnswer(invocation -> {
                Patient patient = invocation.getArgument(0);
                patient.setPatientID(13l); // Simulate saving with a generated ID
                return patient;
            });

        when(modelMapper.map(eq(patientDTO), eq(Patient.class)))
            .thenReturn(new Patient());

        ResponseEntity<PatientResponseDTO> responseEntity = patientService.createPatient(patientDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testCreatePatient_Conflict() {
        // Create a test patientDTO
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("Test Patient");
        patientDTO.setPhone("1234567890");

        when(patientRepository.findByPatientNameAndPatientPhone(anyString(), anyString()))
            .thenReturn(Optional.of(new Patient()));

        ResponseEntity<PatientResponseDTO> responseEntity = patientService.createPatient(patientDTO);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}
