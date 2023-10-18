package com.booking.api.repository;

import com.booking.api.model.Booking;
import com.booking.api.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface BookingRepository  extends JpaRepository<Booking, Long> {


    @Modifying
    @Query("UPDATE Booking b SET b.date = :newDate, b.time = :newTime, b.bookingStatus = :newBookingStatus WHERE b.id = :bookingId")
    void updateDateAndTimeAndStatusById(
        @Param("newDate") LocalDate newDate,
        @Param("newTime") LocalTime newTime,
        @Param("newBookingStatus") BookingStatus newBookingStatus,
        @Param("bookingId") Long bookingId
    );
}
