package com.booking.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PatientResponseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    // Private constructor to enforce builder usage
    private PatientResponseDTO() {}

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
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

        public PatientResponseDTO build() {
            return responseDTO;
        }
    }

    // Static method to obtain a new builder
    public static Builder builder() {
        return new Builder();
    }
}
