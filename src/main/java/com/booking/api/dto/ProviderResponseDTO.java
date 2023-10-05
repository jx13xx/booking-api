package com.booking.api.dto;

import com.booking.api.model.Provider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
public class ProviderResponseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<String, String> provider;

    private ProviderResponseDTO() {}

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getProvider(){
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
            HashMap<String, String> response = new HashMap<>();
            response.put("id", provider.getProviderID().toString());
            response.put("name", provider.getProviderName());
            response.put("email", provider.getProviderEmail());
            response.put("phone", provider.getProviderPhone());
            response.put("specialization", provider.getProviderSpecialization());

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
