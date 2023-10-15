package com.booking.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerID;

    private String providerName;

    private String providerSpecialization;

    private String providerEmail;

    private String providerPhone;

    @ElementCollection
    @CollectionTable(name = "working_hours", joinColumns = @JoinColumn(name = "provider_id"))
    private List<WorkingHours> workingHours;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    private Integer consulationDuration;

}
