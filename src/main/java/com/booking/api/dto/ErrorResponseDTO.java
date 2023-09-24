package com.booking.api.dto;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String message;
    private int errorCode;

}
