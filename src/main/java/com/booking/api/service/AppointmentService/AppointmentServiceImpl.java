package com.booking.api.service.AppointmentService;

import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import com.booking.api.validations.BookingValidationParams.ValidateBookingParams;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentServiceAPI {

    @Override
    @ValidateBookingParams
    public ResponseEntity<AppointmentResponseDTO> createAppointment(AppointmentDTO appointmentDTO) {

        System.out.println("AppointmentServiceImpl.createAppointment" + appointmentDTO);
        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO();
        appointmentResponseDTO.setCode("401");
        appointmentResponseDTO.setResponse("Appointment created successfully");
        return ResponseEntity.status(201).body(appointmentResponseDTO);
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
