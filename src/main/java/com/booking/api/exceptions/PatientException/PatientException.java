package com.booking.api.exceptions.PatientException;

@HandleErrorResponse
public class PatientException extends RuntimeException{
    public PatientException(String message){
        super(message);
    }
}
