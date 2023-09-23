package com.booking.api.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

   private UUID patientID;

   private String patientName;

   private String patientEmail;

   private String patientPhone;

   private LocalDate patientDateOfBirth;

   private Gender patientGender;

   private String patientMedicalHistory;
}
