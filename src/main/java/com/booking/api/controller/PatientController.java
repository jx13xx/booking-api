package com.booking.api.controller;

import com.booking.api.dto.PatientDTO;
import com.booking.api.dto.PatientResponseDTO;
import com.booking.api.exceptions.PatientException.PatientException;
import com.booking.api.exceptions.validation.ValidationErrorHandler;
import com.booking.api.service.PatientService.PatientServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {
    @Autowired
    private PatientServiceAPI patientServiceAPI;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidationErrorHandler
    public ResponseEntity<?> registerNewPatient(@Valid @RequestBody PatientDTO patientDTORq) {
        PatientResponseDTO patientResponseDTO = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            ResponseEntity<PatientResponseDTO> clientResponse = patientServiceAPI.createPatient(patientDTORq);
            if (clientResponse.getStatusCode().is2xxSuccessful()) {
                patientResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();

            } else if (clientResponse.getStatusCode().is4xxClientError()) {
                patientResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();

            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }


        } catch (PatientException ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            throw ex;
        }
        return ResponseEntity.status(httpStatus).body(patientResponseDTO);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retrievePatient(@PathVariable String id) {
        HttpStatus httpStatus = HttpStatus.OK;
        PatientResponseDTO patientResponseDTO = null;

        try {
            ResponseEntity<PatientResponseDTO> clientResponse = patientServiceAPI.retrievePatient(id);
            if (clientResponse.getStatusCode().is2xxSuccessful()) {
                patientResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();

            } else if (clientResponse.getStatusCode().is4xxClientError()) {
                patientResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();

            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }

        } catch (PatientException ex) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            throw ex;
        }

        return ResponseEntity.status(httpStatus).body(patientResponseDTO);
    }







}
