package com.booking.api.controller;

import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import com.booking.api.exceptions.validation.ValidationErrorHandler;
import com.booking.api.service.ProviderService.ProviderServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/provider")
public class ProviderController {

    @Autowired
    private ProviderServiceAPI providerServiceAPI;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidationErrorHandler
    public ResponseEntity<?> registerNewProvider(@Valid @RequestBody ProviderDTO providerDTORq){
        ProviderResponseDTO providerResponseDTO = null;
        HttpStatus httpStatus = HttpStatus.OK;
        try{
            ResponseEntity<ProviderResponseDTO> clientResponse = providerServiceAPI.createProvider(providerDTORq);
            if(clientResponse.getStatusCode().is2xxSuccessful()){
                providerResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();
            }else if(clientResponse.getStatusCode().is4xxClientError()){
                providerResponseDTO = clientResponse.getBody();
                httpStatus = (HttpStatus) clientResponse.getStatusCode();
            }else{
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }catch (Exception ex){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            throw ex;
        }
        return ResponseEntity.status(httpStatus).body(providerResponseDTO);
    }
}
