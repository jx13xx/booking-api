package com.booking.api.validations.GenderValidation;

import com.booking.api.model.Gender;
import jakarta.validation.ConstraintValidator;
import static com.booking.api.model.Gender.isValidGenderValue;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {

    @Override
    public void initialize(ValidGender constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String gender, jakarta.validation.ConstraintValidatorContext context) {
        return gender != null && isValidGenderValue(Gender.valueOf(gender));
    }
}
