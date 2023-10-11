package com.booking.api.service;

//mockito configurations
import com.booking.api.constants.Constants;
import com.booking.api.dto.ProviderDTO;
import com.booking.api.dto.ProviderResponseDTO;
import com.booking.api.model.Provider;
import com.booking.api.model.WorkingHours;
import com.booking.api.repository.ProviderRepository;
import com.booking.api.service.ProviderService.ProviderServiceImpl;
import org.junit.Ignore;
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
import java.util.ArrayList;
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

    @Test
    public void test_DeleteProviderSuccess(){
        String testProviderID = "14";
        Provider provider = new Provider();
        provider.setProviderID(Long.valueOf(testProviderID));

        when(providerRepository.findById(provider.getProviderID())).thenReturn(Optional.of(provider));
        doNothing().when(providerRepository).deleteById(provider.getProviderID());

        ProviderResponseDTO providerResponse = providerService.deleteProvider(testProviderID).getBody();

        assertEquals(Constants.DELETED, providerResponse.getMessage());
        assertEquals(200, providerResponse.getStatus());

        //Verify that deleteById was called with the correct providerID
        verify(providerRepository, times(1)).deleteById(provider.getProviderID());
    }

    @Test
    public void test_deleteProviderNotSuccess() {
        String testNotProviderID = "99";

        when(providerRepository.findById(Long.valueOf(testNotProviderID))).thenReturn(Optional.empty());
        ProviderResponseDTO response = providerService.deleteProvider(testNotProviderID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PROVIDER_NOT_FOUND, response.getMessage());

    }


    @Test
    public void test_retriveProviderSuccess() {
        String testProviderID = "14";
        Provider provider = new Provider();
        provider.setProviderID(Long.valueOf(testProviderID));
        provider.setProviderName("test name");
        provider.setProviderEmail(generateRandomEmail());
        provider.setProviderPhone(generateRandomMobile());
        provider.setProviderName(generateRandomName());
        provider.setProviderSpecialization("Test Specialization");
        provider.setConsulationDuration(3);

        List<WorkingHours> workingHoursList = new ArrayList<>();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setDayOfTheWeek("Wednesday");
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setBreakTime(Time.valueOf("12:00:00"));
        workingHoursList.add(workingHours);

        provider.setWorkingHours(workingHoursList);

        when(providerRepository.findById(provider.getProviderID())).thenReturn(Optional.of(provider));

        ProviderResponseDTO response = providerService.retrieveProvider(testProviderID).getBody();

        assertEquals(200, response.getStatus());
        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
        assertEquals(provider.getProviderName(), response.getProvider().get("name"));
        assertEquals(provider.getProviderEmail(), response.getProvider().get("email"));
        assertEquals(provider.getProviderPhone(), response.getProvider().get("phone"));
        assertEquals(provider.getProviderSpecialization(), response.getProvider().get("specialization"));
        assertEquals(provider.getConsulationDuration().toString(), response.getProvider().get("duration"));
        assertNotNull(response.getProvider().get("workingHours"));
    }
    @Test
    public void test_retrieveProviderNotSuccess() {
        String testNotProviderID = "99";

        when(providerRepository.findById(Long.valueOf(testNotProviderID))).thenReturn(Optional.empty());
        ProviderResponseDTO response = providerService.retrieveProvider(testNotProviderID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PROVIDER_NOT_FOUND, response.getMessage());
    }

    @Test
    @Ignore
    public void test_retrieveProviderNotSuccess2() {
        String testNotProviderID = "99";

        when(providerRepository.findById(Long.valueOf(testNotProviderID))).thenReturn(Optional.empty());
        ProviderResponseDTO response = providerService.retrieveProvider(testNotProviderID).getBody();

        assertEquals(404, response.getStatus());
        assertEquals(Constants.PROVIDER_NOT_FOUND, response.getMessage());
    }

    @Test
    public void testUpdateProviderSuccess() {
        Long providerID = 14l;
        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.setName("test name");
        providerDTO.setEmail(generateRandomEmail());
        providerDTO.setPhone(generateRandomMobile());
        providerDTO.setSpecialization("Test Specialization");
        providerDTO.setDuration(3);

        List<WorkingHours> workingHoursList = new ArrayList<>();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setBreakTime(Time.valueOf("12:00:00"));
        workingHours.setStartTime(Time.valueOf("09:00:00"));
        workingHours.setWorkingDate(LocalDate.now());
        workingHours.setEndTime(Time.valueOf("17:00:00"));
        workingHours.setDayOfTheWeek("Wednesday");
        workingHoursList.add(workingHours);
        providerDTO.setWorkingHours(workingHoursList);

        Provider existingProvider = new Provider();
        existingProvider.setProviderID(providerID);
        existingProvider.setProviderSpecialization("Test Specialization");
        existingProvider.setProviderEmail(providerDTO.getEmail());
        existingProvider.setProviderName(providerDTO.getName());
        existingProvider.setProviderPhone(providerDTO.getPhone());
        existingProvider.setConsulationDuration(providerDTO.getDuration());
        existingProvider.setWorkingHours(workingHoursList);

        when(providerRepository.findById(providerID)).thenReturn(Optional.of(existingProvider));

        ProviderResponseDTO response = providerService.updateProvider(providerDTO, providerID.toString()).getBody();

        //Assert
        verify(providerRepository, times(1)).findById(providerID);
        verify(providerRepository, times(1)).save(any(Provider.class));

        assertEquals(Constants.PATIENT_RETRIEVED, response.getMessage());
    }

    @Test
    public void test_updateProviderNotSuccess(){
       String testNotProvideID = "99";
       ProviderDTO providerDTO = new ProviderDTO();
       providerDTO.setName("test name");
       providerDTO.setEmail("test@mail.com");
       providerDTO.setPhone("971527206148");
       providerDTO.setSpecialization("Test Specialization");

         when(providerRepository.findById(Long.valueOf(testNotProvideID))).thenReturn(Optional.empty());
         ProviderResponseDTO response = providerService.updateProvider(providerDTO,testNotProvideID).getBody();

         assertEquals(404, response.getStatus());
         assertEquals(Constants.PROVIDER_NOT_FOUND, response.getMessage());
    }

}

