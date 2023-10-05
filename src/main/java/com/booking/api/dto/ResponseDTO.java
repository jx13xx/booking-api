package com.booking.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

public class ResponseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    public ResponseDTO() {
        // Initialize default values if needed
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public static class Builder {

        private final ResponseDTO responseDTO = new ResponseDTO();

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

        public ResponseDTO build(){
            return responseDTO;
        }
    }

    public static Builder builder() {return new Builder(); }


}
