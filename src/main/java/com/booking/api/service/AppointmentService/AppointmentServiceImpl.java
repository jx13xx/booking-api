package com.booking.api.service.AppointmentService;

import com.booking.api.constants.Constants;
import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import com.booking.api.model.*;
import com.booking.api.repository.PatientRepository;
import com.booking.api.repository.ProviderRepository;
import com.booking.api.validations.BookingValidationParams.ValidateBookingParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentServiceAPI {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    @ValidateBookingParams
    public ResponseEntity<AppointmentResponseDTO> createAppointment(AppointmentDTO appointmentDTO) {
        Optional<Provider> provider = getProvider(appointmentDTO);
        Optional<Patient> patient = getPatient(appointmentDTO);

        if (!isBookingDateAvailable(appointmentDTO, provider)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_DATE));
        }

        if (!isBookingTimeAvailable(appointmentDTO, provider)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_TIME));
        }

        if (isBookingConflicting(appointmentDTO, provider)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_NOT_AVAILABLE));
        }

        return proceedBooking(appointmentDTO, provider, patient);
   }

    private ResponseEntity<AppointmentResponseDTO> proceedBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient) {
        saveBooking(appointmentDTO, provider, patient);
        return ResponseEntity.status(201).body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_SAVE_SUCCESSFULLY));
    }

    private void saveBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient) {
        Booking booking = new Booking();
        booking.setTime(appointmentDTO.getTime().toLocalTime());
        booking.setDate(appointmentDTO.getDate());
        booking.setPatientId(patient.get().getPatientID());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setProvider(provider.get());

        provider.get().getBookings().add(booking);
        providerRepository.save(provider.get());
    }


    private boolean isBookingDateAvailable(AppointmentDTO appointmentDTO, Optional<Provider> provider) {
        return provider.map(p -> p.getWorkingHours().stream()
                .anyMatch(workingHours -> workingHours.getWorkingDate().isEqual(appointmentDTO.getDate()) && workingHours.isAvailable()))
            .orElse(false);
    }

    private boolean isBookingTimeAvailable(AppointmentDTO appointmentDTO, Optional<Provider> providerOptional) {
        Time bookingTime = appointmentDTO.getTime();
        LocalTime bookingTimeLocal = bookingTime.toLocalTime();

        if(providerOptional.isPresent()){
            Provider provider = providerOptional.get();
            for(WorkingHours workingHours: provider.getWorkingHours()){
                if(workingHours.isAvailable()){
                    if(isBetween(bookingTimeLocal, workingHours.getStartTime().toLocalTime(), workingHours.getEndTime().toLocalTime())){
                        return true;
                    }
                }
            }
        }
        return false;

    }

    private boolean isBookingConflicting(AppointmentDTO appointmentDTO, Optional<Provider> providerOptional) {
        LocalDate bookingDate = appointmentDTO.getDate();
        LocalTime bookingTime = appointmentDTO.getTime().toLocalTime();

        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            for (Booking booking : provider.getBookings()) {
                if (booking.getProvider().getProviderID().equals(provider.getProviderID()) &&
                    booking.getDate().isEqual(bookingDate) &&
                    booking.getTime().equals(bookingTime)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);
    }

    private Optional<Provider> getProvider(AppointmentDTO appointmentDTO) {
      return providerRepository.findById(Long.valueOf(appointmentDTO.getProviderID()));
    }

    private Optional<Patient> getPatient(AppointmentDTO appointmentDTO) {
        return patientRepository.findById(Long.valueOf(appointmentDTO.getPatientID()));
    }

    private AppointmentResponseDTO buildResponseDTO(AppointmentDTO appointmentDTO, Provider provider, Patient patient,  String message) {
        if(message.equals(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_DATE)){
            return new AppointmentResponseDTO.Builder()
                .withMessage(message)
                .withCode(404)
                .withProvider(provider, true)
                .build();
        }else if(message.equals(Constants.BOOKING_NOT_AVAILABLE_FOR_PARTICULAR_TIME)){
            return new AppointmentResponseDTO.Builder()
                .withMessage(message)
                .withCode(404)
                .withProvider(provider, true)
                .build();
        } else if(message.equals(Constants.BOOKING_NOT_AVAILABLE)){
            return new AppointmentResponseDTO.Builder()
                .withMessage(message)
                .withCode(403)
                .withProvider(provider, true)
                .build();
        }
        return new AppointmentResponseDTO.Builder()
            .withMessage(message)
            .withCode(201)
            .withAppointmentDate(appointmentDTO.getDate())
            .withAppointmentTime(appointmentDTO.getTime())
            .withProvider(provider)
            .withPatient(patient)
            .build();
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> retrieveAppointment(String id) {
        return null;
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> deleteAppointment(String id) {
        return null;
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(AppointmentDTO appointmentDTO, String id) {
        return null;
    }
}
