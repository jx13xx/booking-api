package com.booking.api.validations.UAENumberValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UAEPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUAEPhone {
    String message () default "Invalid UAE phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
