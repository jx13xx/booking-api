package com.booking.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

@Data
@Embeddable
public class WorkingHours {

    private LocalDate workingDate;
    private String dayOfTheWeek;
    private Time startTime;
    private Time endTime;
    private Time breakTime;

    private Integer maxSlots = 10;
    private boolean isAvailable = true;
}
