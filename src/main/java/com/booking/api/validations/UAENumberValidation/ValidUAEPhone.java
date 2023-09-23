package com.booking.api.validations.UAENumberValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UAEPhoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUAEPhone {
    String message () default "Invalid UAE phone number entered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
