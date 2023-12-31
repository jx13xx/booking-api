package com.booking.api.service;

import com.booking.api.constants.Constants;
import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import com.booking.api.model.Gender;
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
import static org.mockito.Mockito.*;

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

    @Test
    public void test_retrievePatientSucces(){
        String testPatientID = "14";
        Patient patient = new Patient();
        patient.setPatientID(Long.valueOf(testPatientID));
        patient.setPatientGender(Gender.MALE);
        patient.setPatientEmail("testmail@mail.com");
        patient.setPatientPhone("9715292038");
        patient.setPatientName("test name");

        when(patientRepository.findById(patient.getPatientID())).thenReturn(Optional.of(patient));

        PatientResponseDTO response =  patientService.retrievePatient(testPatientID).getBody();

        assertEquals(200, response.getStatus());
        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
        assertEquals(patient.getPatientName(), response.getPatient().get("name"));
        assertEquals(patient.getPatientEmail(), response.getPatient().get("email"));
    }

    @Test
    public void test_retrievePatientNotSuccess(){
        String testNotPatientID = "99";

        when(patientRepository.findById(Long.valueOf(testNotPatientID))).thenReturn(Optional.empty());
        PatientResponseDTO response = patientService.retrievePatient(testNotPatientID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
    }

    @Test
    public void test_deletePatientSuccess() {
        String testPatientID = "14";
        Patient patient = new Patient();
        patient.setPatientID(Long.valueOf(testPatientID));
        patient.setPatientGender(Gender.MALE);
        patient.setPatientEmail("testmail@mail.com");
        patient.setPatientPhone("9715292038");
        patient.setPatientName("test name");

        when(patientRepository.findById(patient.getPatientID())).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).deleteById(patient.getPatientID());

        PatientResponseDTO response = patientService.deletePatient(testPatientID).getBody();

        assertEquals(200, response.getStatus());
        assertEquals(Constants.DELETED, response.getMessage());

        // Verify that deleteById was called with the correct patient ID
        verify(patientRepository).deleteById(patient.getPatientID());

    }

    @Test
    public void test_deletePatientNotSuccess(){
        String testNotPatientID = "99";

        when(patientRepository.findById(Long.valueOf(testNotPatientID))).thenReturn(Optional.empty());
        PatientResponseDTO response = patientService.deletePatient(testNotPatientID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
    }

    @Test
    public void testUpdatePatientSuccess() {
        Long patientId = 1L;
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("testname");
        patientDTO.setGender(String.valueOf(Gender.valueOf("MALE")));
        patientDTO.setEmail("test@mail.com");
        patientDTO.setPhone("971527206148");
        patientDTO.setMedicalHistory("NU");

        Patient existingPatient = new Patient();
        existingPatient.setPatientID(patientId);
        existingPatient.setPatientName("testname");
        existingPatient.setPatientGender(Gender.valueOf("MALE"));
        existingPatient.setPatientEmail("test@mail.com");
        existingPatient.setPatientPhone("971527206148");
        existingPatient.setPatientMedicalHistory("Updated details");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));

        // Act
        PatientResponseDTO response = patientService.updatePatient(patientDTO, patientId.toString()).getBody();

        // Assert
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).save(any(Patient.class));

        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
    }

    @Test
    public void test_updatePatientNotSucess(){
        String testNotPatientID = "99";
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("testname");
        patientDTO.setGender(String.valueOf(Gender.valueOf("MALE")));
        patientDTO.setEmail("test@mail.com");
        patientDTO.setPhone("971527206148");

        when(patientRepository.findById(Long.valueOf(testNotPatientID))).thenReturn(Optional.empty());
        PatientResponseDTO response = patientService.updatePatient(patientDTO,testNotPatientID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
    }
}
