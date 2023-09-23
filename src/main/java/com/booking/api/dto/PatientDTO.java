package com.booking.api.dto;

import com.booking.api.model.Gender;
import com.booking.api.validations.GenderValidation.ValidGender;
import com.booking.api.validations.UAENumberValidation.ValidUAEPhone;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PatientDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot be more than 50 characters long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Size(max = 32, message = "Email can be up to to 32 characters long")
    private String email;

    @NotBlank(message = "Phone is required")
    @ValidUAEPhone
    private String phone;

    @PastOrPresent(message = "Date of Birth cannot be a future date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @ValidGender
    private String gender;

    private String medicalHistory;


}
