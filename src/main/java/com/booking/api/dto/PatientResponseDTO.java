package com.booking.api.dto;

import com.booking.api.model.Patient;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

public class PatientResponseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<String, String> patient;

    // Private constructor to enforce builder usage
    private PatientResponseDTO() {}

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getPatient(){
        return patient;
    }

    public Integer getStatus() {
        return status;
    }

    // Builder pattern class
    public static class Builder {
        private final PatientResponseDTO responseDTO = new PatientResponseDTO();

        public Builder withId(String id) {
            responseDTO.id = id;
            return this;
        }

        public Builder withMessage(String message) {
            responseDTO.message = message;
            return this;
        }

        public Builder withStatus(Integer status) {
            responseDTO.status = status;
            return this;
        }

        public Builder withPatient(Patient patient){
            HashMap<String, String> response = new HashMap<>();
            response.put("id", patient.getPatientID().toString());
            response.put("name", patient.getPatientName());
            response.put("email", patient.getPatientEmail());
            response.put("phone", patient.getPatientPhone());
            response.put("gender", patient.getPatientGender().toString());
            response.put("medicalHistory", patient.getPatientMedicalHistory());

            responseDTO.patient = response;
            return this;
        }

        public PatientResponseDTO build() {
            return responseDTO;
        }
    }

    // Static method to obtain a new builder
    public static Builder builder() {
        return new Builder();
    }
}
