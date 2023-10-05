package com.booking.api.service;

//mockito configurations
import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import com.booking.api.model.Provider;
import com.booking.api.model.WorkingHours;
import com.booking.api.repository.ProviderRepository;
import com.booking.api.service.ProviderService.ProviderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class ProviderServiceImplTest {

    public String generateRandomEmail() {
        String email = "test" + Math.random() + "@mail.com";
        return email;
    }

    public String generateRandomMobile() {
        String mobile = "971" + new Random().nextInt(100000000);
        return mobile;
    }

    public String generateRandomName() {
        String name = "test" + Math.random();
        return name;
    }


    @InjectMocks
    private ProviderServiceImpl providerService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProviderRepository providerRepository;

    @Test
    public void testCreateProvider_Success() {
        String providerName = generateRandomName();
        String providerEmail = generateRandomEmail();
        String providerNumber = generateRandomMobile();

        ProviderDTO providerDTO = new ProviderDTO();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));

        List<WorkingHours> workingHoursList = List.of(workingHours);


        providerDTO.setName(providerName);
        // setEmail should always have random email address
        providerDTO.setEmail(providerEmail);
        providerDTO.setDuration(3);
        providerDTO.setPhone(providerNumber);
        providerDTO.setSpecialization("Test Specialization");
        providerDTO.setWorkingHours(workingHoursList);

        when(providerRepository.findByProviderNameAndProviderEmail(providerName, providerEmail))
            .thenReturn(Optional.empty());
        when(providerRepository.save(any(Provider.class)))
            .thenAnswer(invocation -> {
                Provider provider = invocation.getArgument(0);
                provider.setProviderID(13l); // Simulate saving with a generated ID
                return provider;
            });

        when(modelMapper.map(eq(providerDTO), eq(Provider.class)))
            .thenReturn(new Provider());

        ResponseEntity<ProviderResponseDTO> responseEntity = providerService.createProvider(providerDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateProvider_Conflict() {
        String providerName = generateRandomName();
        String providerEmail = generateRandomEmail();
        String providerNumber = generateRandomMobile();

        ProviderDTO providerDTO = new ProviderDTO();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));

        List<WorkingHours> workingHoursList = List.of(workingHours);

        providerDTO.setName(providerName);
        providerDTO.setEmail(providerEmail);
        providerDTO.setPhone(providerNumber);
        providerDTO.setDuration(3);
        providerDTO.setWorkingHours(workingHoursList);

        when(providerRepository.findByProviderNameAndProviderEmail(anyString(), anyString()))
            .thenReturn(Optional.of(new Provider()));

        ResponseEntity<ProviderResponseDTO> responseEntity = providerService.createProvider(providerDTO);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

    }

}

