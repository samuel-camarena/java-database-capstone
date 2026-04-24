package com.project.back_end.services;

import com.project.back_end.exceptions.DoctorRepositoryException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTests {
    @Mock
    private DoctorRepository doctorRepo;
    @Mock
    private AppointmentRepository appointmentRepo;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private DoctorService doctorService;
    
    public Doctor expectedDoc1;
    public Doctor expectedDoc2;
    private Patient expectedPatient1;
    private Appointment appointmentForDoc1AndPatient1;
    
    @BeforeEach
    void setup() {
        this.expectedDoc1 = new Doctor.Builder()
            .id(1L)
            .name("Test Name 1")
            .specialty("Test Specialty 1")
            .email("test1@email.com")
            .password("Password123")
            .phone("1111111111")
            .availableTimes(List.of("10:00-11:00", "11:00-12:00", "14:00-15:00"))
            .yearsOfExperience(1)
            .clinicAddress("Test Clinic Address 1")
            .rating(1.0).build();
        
        this.expectedDoc2 = new Doctor.Builder()
            .id(2L)
            .name("Test Name 2")
            .specialty("Test Specialty 2")
            .email("test2@email.com")
            .password("Password123")
            .phone("2222222222")
            .availableTimes(List.of("11:00-12:00", "14:00-15:00", "15:00-16:00"))
            .yearsOfExperience(2)
            .clinicAddress("Test Clinic Address 2")
            .rating(2.0).build();
        
        this.expectedPatient1 = new Patient.Builder()
            .name("Test Patient name 1")
            .email("test1@email.com")
            .password("Password123")
            .phone("0123456789")
            .address("Test Address 1")
            .dateOfBirth(LocalDate.of(1980, 1, 1))
            .emergencyContact("9876543210")
            .insuranceProvider("Test Insurance Provider name 1").build();
        
        this.appointmentForDoc1AndPatient1 = new Appointment.Builder()
            .doctor(expectedDoc1)
            .patient(expectedPatient1)
            .appointmentTime(LocalDateTime
                .now()
                .plusDays(1)
                .withHour(10).withMinute(0).withSecond(0))
            .status(0)
            .reasonForVisiting("General review")
            .notes("Notes about appointment").build();
    }

    @Nested
    class DoctorMainServiceSuccessfulTests {

        @Test
        void givenAValidIdAndDate_whenGetDoctorAvailability_thenResEntityOkAndBodyAvailableTimeSlotsAtDate() throws Exception {
            when(doctorRepo.getDoctorAvailability(1L, LocalDate.now().plusDays(1))).thenReturn(List.of("1", "2"));
            when(doctorRepo.notExistsById(1L)).thenReturn(false);
            
            ResponseEntity<Map<String, List<String>>> res =
                doctorService.getDoctorAvailability(1L, LocalDate.now().plusDays(1));
            
            assertThat(res).isNotNull();
            assertThat(res.getStatusCode()).isNotNull();
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getBody()).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isNotEmpty();
        }
    }

    @Nested
    class DoctorMainServiceFailTests {
        
        @Test
        void givenANotExistingIdAndDate_whenGetDoctorAvailability_thenResEntityBadRequestAndBodyFailWithEmptyAvailableTimesList() throws Exception {
            when(doctorRepo.notExistsById(anyLong())).thenReturn(true);
            
            ResponseEntity<Map<String, List<String>>> res =
                doctorService.getDoctorAvailability(-1L, LocalDate.now().plusDays(1));
            
            assertThat(res).isNotNull();
            assertThat(res.getStatusCode()).isNotNull();
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(res.getBody()).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isEmpty();
        }
    }
    
    @Nested
    class DoctorMainServiceErrorTests {
        
        @Test
        void givenAInternalError_whenGetDoctorAvailability_thenResEntityBadRequestAndBodyErrorWithEmptyAvailableTimesList() throws Exception {
            when(doctorRepo.notExistsById(anyLong()))
                .thenThrow(new DoctorRepositoryException("notExistsById failed (todo: improve msg)"));
            
            ResponseEntity<Map<String, List<String>>> res =
                doctorService.getDoctorAvailability(1L, LocalDate.now().plusDays(1));

            assertThat(res).isNotNull();
            assertThat(res.getStatusCode()).isNotNull();
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(res.getBody()).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isNotNull();
            assertThat(res.getBody().get("availableTimes")).isEmpty();
        }
    }
}
