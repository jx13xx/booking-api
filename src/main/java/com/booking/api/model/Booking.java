package com.booking.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Entity
public class Booking {

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private Long patientId;
    private String slot;
    private LocalTime time;
    private LocalDate date;
    private String consultationTime;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

}
