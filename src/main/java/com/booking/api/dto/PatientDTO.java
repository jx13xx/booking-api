package com.booking.api.dto;

import com.booking.api.model.Gender;
import com.booking.api.validations.UAENumberValidation.ValidUAEPhone;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;

@Data
public class PatientDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 50 , message = "Name must not be more than 255 characters long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Size(max = 255, message = "Email can be upto to 255 characters long")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @ValidUAEPhone()
    private String phone;

    @PastOrPresent
    @DateTimeFormat(pattern = "dd/MM/YYYY")
    private String dateOfBirth;


    private Gender gender;
    private String medicalHistory;


}
