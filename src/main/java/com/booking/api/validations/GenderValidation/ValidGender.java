package com.booking.api.validations.GenderValidation;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GenderValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {

    String message () default "Invalid Gender Type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

