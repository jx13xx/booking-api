package com.booking.api.dto;

import com.booking.api.model.Patient;
import com.booking.api.model.Provider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

@Data
public class AppointmentResponseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate appointmentDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Time appointmentTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<Object, Object> provider;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<Object,Object> patient;

    private AppointmentResponseDTO() {}

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public HashMap<Object, Object> getProvider(){
        return provider;
    }

    public HashMap<Object, Object> getPatient(){
        return patient;
    }

    public LocalDate getAppointmentDate(){
        return appointmentDate;
    }

    public Time getAppointmentTime(){
        return appointmentTime;
    }

    public static class Builder {
        private final AppointmentResponseDTO responseDTO = new AppointmentResponseDTO();

        public Builder withId(String id) {
            responseDTO.id = id;
            return this;
        }

        public Builder withMessage(String message) {
            responseDTO.message = message;
            return this;
        }

        public Builder withCode(Integer code) {
            responseDTO.code = code;
            return this;
        }


        public Builder withProvider(Provider provider){
            HashMap<Object, Object > response = new HashMap<>();
            response.put("id", provider.getProviderID().toString());
            response.put("name", provider.getProviderName());
            response.put("email", provider.getProviderEmail());
            response.put("phone", provider.getProviderPhone());
            response.put("consultationDuration", provider.getConsulationDuration());
            response.put("specialization", provider.getProviderSpecialization());

            responseDTO.provider = response;
            return this;
        }

        public Builder withProvider(Provider provider, boolean showWorkingHours){
            HashMap<Object, Object > response = new HashMap<>();
            response.put("id", provider.getProviderID().toString());
            response.put("name", provider.getProviderName());
            response.put("specialization", provider.getProviderSpecialization());

            if(showWorkingHours){
                Optional.ofNullable(provider.getWorkingHours()).ifPresent(workingHours -> {
                    List<Map<String, String>> workingHoursList = new ArrayList<>();

                    workingHours.forEach(workingHour -> {
                        HashMap<String, String> workingHourMap = new HashMap<>();
                        workingHourMap.put("workingDate", workingHour.getWorkingDate().toString());
                        workingHourMap.put("dayOfTheWeek", workingHour.getDayOfTheWeek());
                        workingHourMap.put("startTime", workingHour.getStartTime().toString());
                        workingHourMap.put("endTime", workingHour.getEndTime().toString());
                        workingHourMap.put("breakTime", workingHour.getBreakTime().toString());
                        workingHourMap.put("isSlotAvailable", String.valueOf(workingHour.isAvailable()));

                        workingHoursList.add(workingHourMap);
                    });

                    response.put("workingHours", workingHoursList);
                });
            }

            responseDTO.provider = response;
            return this;
        }

        public Builder withPatient(Patient patient){
            HashMap<Object, Object > response = new HashMap<>();
            response.put("id", patient.getPatientID().toString());
            response.put("name", patient.getPatientName());
            response.put("email", patient.getPatientEmail());
            response.put("phone", patient.getPatientPhone());
            response.put("dob", patient.getPatientDateOfBirth());
            response.put("gender", patient.getPatientGender());

            responseDTO.patient = response;
            return this;
        }

        public Builder withAppointmentDate(LocalDate appointmentDate){
            responseDTO.appointmentDate = appointmentDate;
            return this;
        }

        public Builder withAppointmentTime(Time appointmentTime){
            responseDTO.appointmentTime = appointmentTime;
            return this;
        }

        public AppointmentResponseDTO build() {
            return responseDTO;
        }

        public static Builder builder() {
            return new Builder();
        }
    }

}
