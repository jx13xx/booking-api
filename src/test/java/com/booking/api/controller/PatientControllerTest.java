package com.booking.api.controller;

import com.booking.api.constants.Constants;
import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
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
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
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
}
