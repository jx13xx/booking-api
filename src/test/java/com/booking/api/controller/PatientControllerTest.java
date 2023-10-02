package com.booking.api.controller;

import com.booking.api.constants.Constants;
import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import com.booking.api.model.Gender;
import com.booking.api.model.Patient;
import com.booking.api.service.PatientService.PatientServiceAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.json.*;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientServiceAPI patientServiceAPI;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldThrowValidationMessages() throws Exception {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("name");
        patientDTO.setPhone(null);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\"}") // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        JSONObject json = new JSONObject(content);

        assertEquals("Invalid Gender Type", json.getString("gender"));
        assertEquals("Email is required", json.getString("email"));

    }


    @Test
    public void shouldGive200ResponseOnSuccess() throws Exception {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("name");
        patientDTO.setPhone("971527459148");
        patientDTO.setEmail("test@mail.com");
        patientDTO.setGender("MALE");
        patientDTO.setDateOfBirth(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(patientDTO);

        // Create a sample response for the service method
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withId("2")
            .withStatus(201)
            .withMessage(Constants.PATIENT_CREATED_SUCCESSFULLY)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(patientResponseDTO);
        when(patientServiceAPI.createPatient(patientDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_CREATED_SUCCESSFULLY, response.getMessage());
        assertEquals(201, response.getStatus());
        assertEquals("2", response.getId());
    }

    @Test
    public void shouldGive400OnDuplicateCreattion() throws Exception {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("name");
        patientDTO.setPhone("971527459148");
        patientDTO.setEmail("test@mail.com");
        patientDTO.setGender("MALE");
        patientDTO.setDateOfBirth(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(patientDTO);

        // Create a sample response for the service method
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(403)
            .withMessage(Constants.PATIENT_ALREADY_EXITS)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).body(patientResponseDTO);
        when(patientServiceAPI.createPatient(patientDTO)).thenReturn(responseEntity);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_ALREADY_EXITS, response.getMessage());
        assertEquals(403, response.getStatus());

    }

    @Test
    public void shouldGive200ResponseIfPatientFound() throws Exception {
        Patient patient = new Patient();
        patient.setPatientID(Long.valueOf("1"));
        patient.setPatientName("name");
        patient.setPatientPhone("971527459148");
        patient.setPatientEmail("test@mail.com");
        patient.setPatientGender(Gender.MALE);
        patient.setPatientDateOfBirth(LocalDate.now());

        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(200)
            .withMessage(Constants.PATIENT_RETRIEVED)
            .withPatient(patient)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientResponseDTO);
        when(patientServiceAPI.retrievePatient("1")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/patient/{id}", 1)  // Replace 1 with the desired patient ID
            )
            .andExpect(status().isOk())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
        assertEquals(200, response.getStatus());

    }

    @Test
    public void shoudlGive404WhenPatientNotFound() throws Exception{
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(404)
            .withMessage(Constants.PATIENT_NOT_FOUND)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(patientResponseDTO);
        when(patientServiceAPI.retrievePatient("99")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/patient/{id}", 99)  // Replace 1 with the desired patient ID
            )
            .andExpect(status().isNotFound())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldGive200AfterPatientDeleteSuccessfully() throws Exception {
        Patient patient = new Patient();
        patient.setPatientID(Long.valueOf("1"));
        patient.setPatientName("name");
        patient.setPatientPhone("971527459148");
        patient.setPatientEmail("test@mail.com");
        patient.setPatientGender(Gender.MALE);
        patient.setPatientDateOfBirth(LocalDate.now());

        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(200)
            .withMessage(Constants.DELETED)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientResponseDTO);
        when(patientServiceAPI.deletePatient("1")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/patient/{id}", 1)  // Replace 1 with the desired patient ID
            )
            .andExpect(status().isOk())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.DELETED, response.getMessage());
        assertEquals(200, response.getStatus());

    }
    @Test
    public void shoudlGive404DeleteWhenPatientNotFound() throws Exception{
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(404)
            .withMessage(Constants.PATIENT_NOT_FOUND)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(patientResponseDTO);
        when(patientServiceAPI.deletePatient("99")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/patient/{id}", 99)  // Replace 1 with the desired patient ID
            )
            .andExpect(status().isNotFound())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldGive200ResponseOnUpdatePatient() throws Exception {
        Long patientId = 2L;
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("name");
        patientDTO.setPhone("971527459148");
        patientDTO.setEmail("test@mail.com");
        patientDTO.setGender("MALE");
        patientDTO.setDateOfBirth(LocalDate.now());
        patientDTO.setMedicalHistory("NULL");

        Patient patient = new Patient();
        patient.setPatientID(patientId);
        patient.setPatientName(patientDTO.getName());
        patient.setPatientEmail(patientDTO.getEmail());
        patient.setPatientPhone(patientDTO.getPhone());
        patient.setPatientGender(Gender.valueOf(patientDTO.getGender()));
        patient.setPatientDateOfBirth(patientDTO.getDateOfBirth());
        patient.setPatientMedicalHistory(patientDTO.getMedicalHistory());

        String requestJSON = objectMapper.writeValueAsString(patientDTO);

        // Create a sample response for the service method
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withId(patientId.toString())
            .withStatus(200)
            .withMessage(Constants.PATIENT_RETRIEVED)
            .withPatient(patient)
            .build();

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientResponseDTO);
        when(patientServiceAPI.updatePatient(patientDTO, patientId.toString())).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/patient/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(patientDTO.getPhone(), response.getPatient().get("phone"));
        assertEquals(patientDTO.getName(), response.getPatient().get("name"));
        assertEquals(patientDTO.getEmail(), response.getPatient().get("email"));
    }

    @Test
    public void shoudlGive404UpdateWhenPatientNotFound() throws Exception{
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("name");
        patientDTO.setPhone("971527459148");
        patientDTO.setEmail("test@mail.com");
        patientDTO.setGender("MALE");
        patientDTO.setDateOfBirth(LocalDate.now());
        patientDTO.setMedicalHistory("NULL");

        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
            .withStatus(404)
            .withMessage(Constants.PATIENT_NOT_FOUND)
            .build();

        String requestJSON = objectMapper.writeValueAsString(patientDTO);

        ResponseEntity<PatientResponseDTO> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(patientResponseDTO);
        when(patientServiceAPI.updatePatient(patientDTO, "99")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/patient/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        PatientResponseDTO response = objectMapper.readValue(content, PatientResponseDTO.class);

        assertEquals(Constants.PATIENT_NOT_FOUND, response.getMessage());
        assertEquals(404, response.getStatus());
    }
}
