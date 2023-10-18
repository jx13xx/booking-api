package com.booking.api.controller;

import com.booking.api.constants.Constants;
import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import com.booking.api.service.AppointmentService.AppointmentServiceAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Ignore;
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


import java.sql.Time;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentServiceAPI appointmentServiceAPI;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void shouldThrowValidationMessages() throws Exception{
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("1");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointment")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"patientID\":\"20\"}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(content);

        assertEquals("Provider ID is required", jsonObject.get("providerID"));
    }

    @Test
    public void shouldGive200ResponseOnSuccessfulBooking() throws Exception{
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        //Create a sampleResponse
        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withAppointmentTime(appointmentDTO.getTime())
            .withAppointmentDate(appointmentDTO.getDate())
            .withMessage(Constants.BOOKING_SAVE_SUCCESSFULLY)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.CREATED).body(appointmentResponseDTO);
        when(appointmentServiceAPI.createAppointment(appointmentDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn();
        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_SAVE_SUCCESSFULLY, responseDTO.getMessage());
    }

    @Test
    public void shouldGive400WhenBookingTimeNotAvailable() throws Exception {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withMessage(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_TIME)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.NOT_FOUND).body(appointmentResponseDTO);

        when(appointmentServiceAPI.createAppointment(appointmentDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_TIME, responseDTO.getMessage());

    }

    @Test
    public void shouldGive400WhenBookingDateNotAvailable() throws Exception {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withMessage(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_DATE)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.NOT_FOUND).body(appointmentResponseDTO);

        when(appointmentServiceAPI.createAppointment(appointmentDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_DATE, responseDTO.getMessage());

    }

    @Test
    public void should403WhenBookingIsNotAvailable() throws Exception {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withMessage(Constants.BOOKING_NOT_AVAILABLE)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.FORBIDDEN).body(appointmentResponseDTO);

        when(appointmentServiceAPI.createAppointment(appointmentDTO)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_NOT_AVAILABLE, responseDTO.getMessage());

    }

    @Test
    public void shouldGive200AfterAppointmentIsDeleted() throws Exception {
        String id = "10";
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        //Create a sampleResponse
        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withMessage(Constants.BOOKING_DELETED)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.CREATED).body(appointmentResponseDTO);

        when(appointmentServiceAPI.deleteAppointment(id)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/appointment/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_DELETED, responseDTO.getMessage());
    }

    @Test
    public void shouldGive404WhenBookingIsNotFoundForDelted() throws Exception {
        String id = "10";
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientID("10");
        appointmentDTO.setProviderID("20");
        appointmentDTO.setTime(Time.valueOf("10:00:00"));
        appointmentDTO.setDate(LocalDate.now());

        String requestJSON = objectMapper.writeValueAsString(appointmentDTO);

        //Create a sampleResponse
        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO.Builder()
            .withMessage(Constants.BOOKING_NOT_FOUND)
            .build();

        ResponseEntity<AppointmentResponseDTO> responseEntity = ResponseEntity
            .status(HttpStatus.NOT_FOUND).body(appointmentResponseDTO);

        when(appointmentServiceAPI.deleteAppointment(id)).thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/appointment/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        //Validate the response
        String content = result.getResponse().getContentAsString();
        AppointmentResponseDTO responseDTO = objectMapper.readValue(content, AppointmentResponseDTO.class);

        assertEquals(Constants.BOOKING_NOT_FOUND, responseDTO.getMessage());
    }
}
