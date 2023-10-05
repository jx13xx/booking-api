package com.booking.api.exceptions.ProviderException;

import com.booking.api.exceptions.PatientException.HandleErrorResponse;

@HandleErrorResponse
public class ProviderException extends RuntimeException {
    public ProviderException(String message) {
        super(message);
    }
}
