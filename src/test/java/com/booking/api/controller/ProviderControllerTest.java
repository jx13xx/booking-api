package com.booking.api.controller;

import com.booking.api.constants.Constants;
import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import com.booking.api.model.Provider;
import com.booking.api.model.WorkingHours;
import com.booking.api.service.ProviderService.ProviderServiceAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderServiceAPI providerServiceAPI;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldThrowValidationMessages() throws Exception {
        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.setName("Test Provider");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\"}") // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        JSONObject json = new JSONObject(content);

        assertEquals("Name is required", json.get("specialization"));
        assertEquals("Email is required", json.getString("email"));
    }


    @Test
    public void shouldGive200ResponseOnSuccess() throws Exception {
        ProviderDTO providerDTO = new ProviderDTO();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));

        List<WorkingHours> workingHoursList = List.of(workingHours);

        providerDTO.setName("Test Provider");
        providerDTO.setSpecialization("Test Specialization");
        providerDTO.setEmail("test@mail.com");
        providerDTO.setPhone("971123456789");
        providerDTO.setDuration(3);
        providerDTO.setWorkingHours(workingHoursList);

        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(201)
            .withMessage(Constants.PROVIDER_CREATED_SUCCESSFULLY).build();

        ResponseEntity<ProviderResponseDTO> responseEntity = ResponseEntity.status(201).body(providerResponseDTO);

        when(providerServiceAPI.createProvider(providerDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(providerDTO)) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO responseDTO = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(201, responseDTO.getStatus());
        assertEquals(Constants.PROVIDER_CREATED_SUCCESSFULLY, responseDTO.getMessage());
    }

    //create a controller test for 400 response
    @Test
    public void shouldGive400ResponseOnFailure() throws Exception {
        ProviderDTO providerDTO = new ProviderDTO();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));

        List<WorkingHours> workingHoursList = List.of(workingHours);

        providerDTO.setName("Test Provider");
        providerDTO.setSpecialization("Test Specialization");
        providerDTO.setEmail("test@mail.com");
        providerDTO.setPhone("971123456789");
        providerDTO.setDuration(3);
        providerDTO.setWorkingHours(workingHoursList);

        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(403)
            .withMessage(Constants.PATIENT_ALREADY_EXITS).build();

        ResponseEntity<ProviderResponseDTO> responseEntity = ResponseEntity.status(403).body(providerResponseDTO);

        when(providerServiceAPI.createProvider(providerDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(providerDTO)) // JSON representation of the patientDTO
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        // Validate the response
        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO responseDTO = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(403, responseDTO.getStatus());
        assertEquals(Constants.PATIENT_ALREADY_EXITS, responseDTO.getMessage());

    }

    @Test
    public void shouldGive404WhenProviderNotFound() throws Exception {
        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(404)
            .withMessage(Constants.PROVIDER_NOT_FOUND).build();

        ResponseEntity<ProviderResponseDTO> responseEntity = ResponseEntity.status(404).body(providerResponseDTO);
        when(providerServiceAPI.deleteProvider("2")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/provider/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO response = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(Constants.PROVIDER_NOT_FOUND, response.getMessage());
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldGive2002AfterProviderDeletedSuccessfully() throws Exception {
        Provider provider = new Provider();
        provider.setProviderID(2L);
        provider.setProviderName("Test Provider");
        provider.setProviderEmail("test@mail.com");
        provider.setProviderPhone("971123456789");
        provider.setConsulationDuration(3);

        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(200)
            .withMessage(Constants.DELETED).build();

        ResponseEntity<ProviderResponseDTO> responseEntity = ResponseEntity.status(200).body(providerResponseDTO);
        when(providerServiceAPI.deleteProvider("2")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/provider/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO response = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(Constants.DELETED, response.getMessage());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldGive200ResponseIfProviderFound() throws Exception {
        Provider provider = new Provider();
        provider.setProviderID(2L);
        provider.setProviderName("Test Provider");
        provider.setProviderEmail("test@mail.com");
        provider.setProviderPhone("971123456789");
        provider.setConsulationDuration(3);

        List<WorkingHours> workingHoursList = new ArrayList<>();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));
        workingHoursList.add(workingHours);
        provider.setWorkingHours(workingHoursList);

        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(200)
            .withMessage(Constants.PROVIDER_RETRIEVED)
            .withProvider(provider)
            .build();

       when(providerServiceAPI.retrieveProvider("2")).thenReturn(ResponseEntity.status(200).body(providerResponseDTO));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/provider/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO response = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(Constants.PROVIDER_RETRIEVED, response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals(provider.getProviderName(), response.getProvider().get("name"));
        assertEquals(provider.getProviderEmail(), response.getProvider().get("email"));
        assertEquals(provider.getProviderPhone(), response.getProvider().get("phone"));
        assertNotNull(response.getProvider().get("workingHours"));
    }

    @Test
    public void shouldGive404ProviderNotFound() throws Exception {
        ProviderResponseDTO providerResponseDTO = ProviderResponseDTO.builder().withId("2")
            .withStatus(404)
            .withMessage(Constants.PROVIDER_NOT_FOUND).build();

        ResponseEntity<ProviderResponseDTO> responseEntity = ResponseEntity.status(404).body(providerResponseDTO);
        when(providerServiceAPI.retrieveProvider("2")).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/provider/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ProviderResponseDTO responseDTO = objectMapper.readValue(content, ProviderResponseDTO.class);

        assertEquals(Constants.PROVIDER_NOT_FOUND, responseDTO.getMessage());
        assertEquals(404, responseDTO.getStatus());

    }


}
