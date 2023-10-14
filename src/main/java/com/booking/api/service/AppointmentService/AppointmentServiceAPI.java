package com.booking.api.service.AppointmentService;

import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.AppointmentResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AppointmentServiceAPI {

    ResponseEntity<AppointmentResponseDTO> createAppointment(AppointmentDTO appointmentDTO);

    ResponseEntity<AppointmentResponseDTO> retrieveAppointment(String id);

    ResponseEntity<AppointmentResponseDTO> deleteAppointment(String id);

    ResponseEntity<AppointmentResponseDTO> updateAppointment(AppointmentDTO appointmentDTO,String id);
}
