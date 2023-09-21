package com.booking.api.validations.GenderValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GenderValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {

    String message () default "Invalid Gender Type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

