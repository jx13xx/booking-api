package com.booking.api.model;

import java.util.EnumSet;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    public static boolean isValidGenderValue(Gender gender){
        return EnumSet.allOf(Gender.class).contains(gender);
    }
}
