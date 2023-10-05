package com.booking.api.exceptions.ProviderException;

import com.booking.api.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProviderExceptionHandler {

    @ExceptionHandler(ProviderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleProviderException(ProviderException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponseDTO.setMessage(ex.getMessage());
        return errorResponseDTO;

    }
}
