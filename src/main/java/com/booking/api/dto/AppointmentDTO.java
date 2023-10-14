package com.booking.api.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;

@Data
public class AppointmentDTO {

    @NotBlank(message = "Provider ID is required")
    private String providerID;

    @NotBlank(message = "Patient ID is required")
    private String patientID;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Time time;

    private String consulationNotes;

}
