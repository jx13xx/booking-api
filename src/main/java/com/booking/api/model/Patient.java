package com.booking.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Patient {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long patientID;

   private String patientName;

   private String patientEmail;

   private String patientPhone;

   private LocalDate patientDateOfBirth;

   private Gender patientGender;

   private String patientMedicalHistory;
}
