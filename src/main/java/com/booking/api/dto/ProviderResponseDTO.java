package com.booking.api.dto;

import com.booking.api.model.Provider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.*;

@Data
public class ProviderResponseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<Object, Object> provider;

    private ProviderResponseDTO() {}

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<Object, Object> getProvider(){
        return provider;
    }

    public Integer getStatus() {
        return status;
    }

    public static class Builder {
        private final ProviderResponseDTO responseDTO = new ProviderResponseDTO();

        public Builder withId(String id) {
            responseDTO.id = id;
            return this;
        }

        public Builder withMessage(String message) {
            responseDTO.message = message;
            return this;
        }

        public Builder withStatus(Integer status) {
            responseDTO.status = status;
            return this;
        }

        public Builder withProvider(Provider provider){
            HashMap<Object, Object > response = new HashMap<>();
            response.put("providerId", provider.getProviderID().toString());
            response.put("name", provider.getProviderName());
            response.put("email", provider.getProviderEmail());
            response.put("phone", provider.getProviderPhone());
            response.put("specialization", provider.getProviderSpecialization());
            response.put("duration", provider.getConsulationDuration().toString());

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

            responseDTO.provider = response;
            return this;
        }

        public ProviderResponseDTO build() {
            return responseDTO;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
