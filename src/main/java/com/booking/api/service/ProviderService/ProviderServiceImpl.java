package com.booking.api.service.ProviderService;

import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import com.booking.api.model.Provider;
import com.booking.api.repository.ProviderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.booking.api.constants.Constants.PROVIDER_ALREADY_EXITS;
import static com.booking.api.constants.Constants.PROVIDER_NOT_FOUND;

@Service
public class ProviderServiceImpl implements ProviderServiceAPI{

    private final ModelMapper modelMapper;

    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderServiceImpl(ModelMapper modelMapper, ProviderRepository providerRepository) {
        this.modelMapper = modelMapper;
        this.providerRepository = providerRepository;
    }

    @Override
    public ResponseEntity<ProviderResponseDTO> createProvider(ProviderDTO providerDTO) {
        Provider provider = mapProviderDTOToEntity(providerDTO);

        Optional<Provider> existingProvider = getProvider(providerDTO);

        if(existingProvider.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(buildResponseDTO(PROVIDER_ALREADY_EXITS));
        }
        Provider savedProvider = saveProvider(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponseDTO(savedProvider, PROVIDER_ALREADY_EXITS));

    }

    private Optional<Provider> getProvider(ProviderDTO providerDTO) {
        return providerRepository.findByProviderNameAndProviderEmail(providerDTO.getName(), providerDTO.getEmail());
    }

    @Override
    public ResponseEntity<ProviderResponseDTO> retrieveProvider(String id) {
        return null;
    }

    @Override
    public ResponseEntity<ProviderResponseDTO> deleteProvider(String id) {
        return null;
    }

    @Override
    public ResponseEntity<ProviderResponseDTO> updateProvider(ProviderDTO providerDTO, String id) {
        return null;
    }

    private Provider mapProviderDTOToEntity(ProviderDTO providerDTO) {
        Provider provider = modelMapper.map(providerDTO, Provider.class);
        try{
            Optional.of(providerDTO.getDuration()).ifPresent(provider::setConsulationDuration);
            provider.setWorkingHours(providerDTO.getWorkingHours());

        }catch (NullPointerException ignored){

        }
        return provider;

    }

    private Provider saveProvider(Provider provider) {
        return providerRepository.save(provider);
    }
    private ProviderResponseDTO buildResponseDTO(Provider provider) {
        return new ProviderResponseDTO.Builder()
                .withId(provider.getProviderID().toString())
                .withProvider(provider)
                .build();
    }
    private ProviderResponseDTO buildResponseDTO(Provider provider, String message) {
        return new ProviderResponseDTO.Builder()
                .withId(provider.getProviderID().toString())
                .withMessage(message)
                .withStatus(HttpStatus.CREATED.value())
                .build();
    }

    private ProviderResponseDTO buildResponseDTO(String message) {
        if (message.equals(PROVIDER_ALREADY_EXITS)) {
            return new ProviderResponseDTO.Builder()
                .withMessage(message)
                .withStatus(HttpStatus.CONFLICT.value())
                .build();
        }
        if (message.equals(PROVIDER_NOT_FOUND)) {
            return new ProviderResponseDTO.Builder()
                .withMessage(message)
                .withStatus(HttpStatus.NOT_FOUND.value())
                .build();
        }
        return new ProviderResponseDTO.Builder()
            .withMessage(message)
            .withStatus(HttpStatus.OK.value())
            .build();
    }
}
