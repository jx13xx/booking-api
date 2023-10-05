package com.booking.api.dto;


import com.booking.api.model.WorkingHours;
import com.booking.api.validations.UAENumberValidation.ValidUAEPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProviderDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot be more than 50 characters long")
    private String name;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot be more than 50 characters long")
    private String specialization;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Size(max = 32, message = "Email can be up to to 32 characters long")
    private String email;

    @NotBlank(message = "Phone is required")
    @ValidUAEPhone
    private String phone;

    private List<WorkingHours> workingHours;

    private int duration;


}
