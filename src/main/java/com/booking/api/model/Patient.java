package com.booking.api.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

   private UUID patientID;

   private String patientName;

   private String patientEmail;

   private String patientPhone;

   private Date patientDOB;

   private Gender patientGender;

   private String patientMedicalHistory;
}
