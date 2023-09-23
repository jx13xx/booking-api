package com.booking.api.validations.UAENumberValidation;
import jakarta.validation.ConstraintValidator;
public class UAEPhoneValidator implements ConstraintValidator<ValidUAEPhone, String> {

    @Override
    public void initialize(ValidUAEPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext constraintValidatorContext) {
        return value != null  && value.matches("^971\\d{9}$");
    }
}
