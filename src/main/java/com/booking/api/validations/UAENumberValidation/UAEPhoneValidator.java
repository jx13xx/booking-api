package com.booking.api.validations.UAENumberValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UAEPhoneValidator implements ConstraintValidator<ValidUAEPhone, String> {

    @Override
    public void initialize(ValidUAEPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null  && value.matches("^971\\d{9}$");
    }
}
