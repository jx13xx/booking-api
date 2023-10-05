package com.booking.api.service.ProviderService;


import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ProviderServiceAPI {


    ResponseEntity<ProviderResponseDTO> createProvider(ProviderDTO providerDTO);

    ResponseEntity<ProviderResponseDTO> retrieveProvider(String id);

    ResponseEntity<ProviderResponseDTO> deleteProvider(String id);

    ResponseEntity<ProviderResponseDTO> updateProvider(ProviderDTO providerDTO, String id);

}
