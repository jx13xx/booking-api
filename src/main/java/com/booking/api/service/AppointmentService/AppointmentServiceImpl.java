package com.booking.api.service.AppointmentService;

import com.booking.api.constants.Constants;
import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import com.booking.api.model.*;
import com.booking.api.repository.BookingRepository;
import com.booking.api.repository.PatientRepository;
import com.booking.api.repository.ProviderRepository;
import com.booking.api.validations.BookingValidationParams.ValidateBookingParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private BookingRepository bookingRepository;

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

        return proceedBooking(appointmentDTO, provider, patient, BookingStatus.PENDING);
   }

    private ResponseEntity<AppointmentResponseDTO> proceedBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient, BookingStatus bookingStatus) {
        saveBooking(appointmentDTO, provider, patient, bookingStatus);
        return ResponseEntity.status(201).body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_SAVE_SUCCESSFULLY));
    }

    private ResponseEntity<AppointmentResponseDTO> proceedBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient, BookingStatus bookingStatus, String id) {
        updateBooking(appointmentDTO, provider, patient, bookingStatus, Long.valueOf(id));
        return ResponseEntity.status(200).body(buildResponseDTO(appointmentDTO, provider.get(), patient.get(), Constants.BOOKING_UPDATED_SUCCESSFULLY));
    }

    private void saveBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient, BookingStatus bookingStatus) {
        Booking booking = new Booking();
        booking.setTime(appointmentDTO.getTime().toLocalTime());
        booking.setDate(appointmentDTO.getDate());
        booking.setPatientId(patient.get().getPatientID());
        booking.setBookingStatus(bookingStatus);
        booking.setProvider(provider.get());

        provider.get().getBookings().add(booking);
        providerRepository.save(provider.get());
    }

    private void updateBooking(AppointmentDTO appointmentDTO, Optional<Provider> provider, Optional<Patient> patient, BookingStatus bookingStatus, Long bookingId) {
        bookingRepository.updateDateAndTimeAndStatusById(appointmentDTO.getDate(), appointmentDTO.getTime().toLocalTime(), bookingStatus, bookingId);
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
        } else if(message.equals(Constants.BOOKING_SAVE_SUCCESSFULLY)){
            return new AppointmentResponseDTO.Builder()
                .withMessage(message)
                .withCode(201)
                .withId(provider.getBookings().get(0).getId().toString())
                .withAppointmentDate(appointmentDTO.getDate())
                .withAppointmentTime(appointmentDTO.getTime())
                .withProvider(provider)
                .withPatient(patient)
                .build();
        }
        return new AppointmentResponseDTO.Builder()
            .withMessage(message)
            .withCode(200)
            .withId(provider.getBookings().get(0).getId().toString())
            .withAppointmentDate(appointmentDTO.getDate())
            .withAppointmentTime(appointmentDTO.getTime())
            .withProvider(provider)
            .withPatient(patient)
            .build();
    }

    private AppointmentResponseDTO buildResponseDTO(Booking booking,String message) {
        return new AppointmentResponseDTO.Builder()
            .withMessage(message)
            .withBooking(booking)
            .withProvider(booking.getProvider())
            .withCode(200)
            .build();
    }

    private AppointmentResponseDTO buildResponseDTO(String message) {
        return new AppointmentResponseDTO.Builder()
            .withMessage(message)
            .withCode(message.equals(Constants.BOOKING_NOT_FOUND) ? 404 : 200)
            .build();
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> retrieveAppointment(String id) {
         try {
                Long providerID = Long.parseLong(id);
                Optional<Booking> booking = bookingRepository.findById(providerID);

                return booking.map(p -> {
                    return ResponseEntity.ok().body(buildResponseDTO(p, Constants.BOOKING_RETREIVED));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponseDTO(Constants.BOOKING_NOT_FOUND)));


         }catch (NumberFormatException ex) {
             return ResponseEntity.badRequest().body(buildResponseDTO("Invalid provider ID format"));
         }
    }

    @Override
    public ResponseEntity<AppointmentResponseDTO> deleteAppointment(String id) {
        try {
            Long providerID = Long.parseLong(id);
            Optional<Booking> booking = bookingRepository.findById(providerID);

            return booking.map(p -> {
                bookingRepository.delete(p);
                return ResponseEntity.ok().body(buildResponseDTO(Constants.BOOKING_DELETED));
            }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponseDTO(Constants.BOOKING_NOT_FOUND)));

        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().body(buildResponseDTO("Invalid provider ID format"));
        }
    }

    @Override
    @ValidateBookingParams
    @Transactional
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(AppointmentDTO appointmentDTO, String id) {
        Optional<Provider> provider = getProvider(appointmentDTO);
        Optional<Patient> patient = getPatient(appointmentDTO);
        ResponseEntity<AppointmentResponseDTO> BOOKING_NOT_FOUND = retrieveBookingWithId(id);
        if (BOOKING_NOT_FOUND != null) return BOOKING_NOT_FOUND;

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

        return proceedBooking(appointmentDTO, provider, patient, BookingStatus.UPDATED, id);

    }

    private ResponseEntity<AppointmentResponseDTO> retrieveBookingWithId(String id) {
        ResponseEntity<AppointmentResponseDTO> retirevedBooking = retrieveAppointment(id);

        if(retirevedBooking.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponseDTO(Constants.BOOKING_NOT_FOUND));
        }
        return null;
    }
}
