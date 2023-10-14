package com.booking.api.validations.BookingValidationParams;

import com.booking.api.dto.AppointmentDTO;
import com.booking.api.dto.ErrorResponseDTO;
import com.booking.api.exceptions.NotFoundException.NotFoundException;
import com.booking.api.repository.PatientRepository;
import com.booking.api.repository.ProviderRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.booking.api.constants.Constants.PATIENT_NOT_FOUND;
import static com.booking.api.constants.Constants.PROVIDER_NOT_FOUND;

@Component
@Aspect
public class ValidationBookingParamsAspect {

    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public ValidationBookingParamsAspect(ProviderRepository providerRepository, PatientRepository patientRepository) {
        this.providerRepository = providerRepository;
        this.patientRepository = patientRepository;
    }

    @Before("@annotation(com.booking.api.validations.BookingValidationParams.ValidateBookingParams) && args(appointmentDTO)")
    public void validateBookingParams(AppointmentDTO appointmentDTO) {
        if (StringUtils.hasText(appointmentDTO.getProviderID()) && !providerRepository.findById(Long.valueOf(appointmentDTO.getProviderID())).isPresent()){
            throw new NotFoundException(HttpStatus.NOT_FOUND, PROVIDER_NOT_FOUND);
        }

        if (StringUtils.hasText(appointmentDTO.getPatientID()) && !patientRepository.findById(Long.valueOf(appointmentDTO.getPatientID())).isPresent()){
            throw new NotFoundException(HttpStatus.NOT_FOUND, PATIENT_NOT_FOUND);
        }

    }

}
