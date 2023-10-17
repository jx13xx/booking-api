package com.booking.api.controller;

import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import com.booking.api.exceptions.validation.ValidationErrorHandler;
import com.booking.api.service.AppointmentService.AppointmentServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentServiceAPI appointmentServiceAPI;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidationErrorHandler
    public ResponseEntity<?> createNewAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO){
        AppointmentResponseDTO appointmentResponseDTO = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            ResponseEntity<AppointmentResponseDTO> appointmentResponse = appointmentServiceAPI.createAppointment(appointmentDTO);
            if(appointmentResponse.getStatusCode().is2xxSuccessful()){
                appointmentResponseDTO = appointmentResponse.getBody();
                httpStatus = (HttpStatus) appointmentResponse.getStatusCode();
            }else if(appointmentResponse.getStatusCode().is4xxClientError()){
                appointmentResponseDTO = appointmentResponse.getBody();
                httpStatus = (HttpStatus) appointmentResponse.getStatusCode();
            }else{
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }

        }catch (Exception ex){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            throw ex;
        }

        return ResponseEntity.status(httpStatus).body(appointmentResponseDTO);

    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retrieveAppoint(@PathVariable String id) {
        HttpStatus httpStatus = HttpStatus.OK;
        AppointmentResponseDTO appointmentResponseDTO = null;

        try {
            ResponseEntity<AppointmentResponseDTO> clientResponse = appointmentServiceAPI.retrieveAppointment(id);
            if(clientResponse.getStatusCode().is2xxSuccessful()) {
                appointmentResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();
            } else if (clientResponse.getStatusCode().is4xxClientError()) {
                appointmentResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }

        }catch (Exception ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            throw ex;
        }
            return ResponseEntity.status(httpStatus).body(appointmentResponseDTO);
    }

}
