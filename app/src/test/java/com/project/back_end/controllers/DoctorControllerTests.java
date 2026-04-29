package com.project.back_end.controllers;

import com.project.back_end.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DoctorService doctorService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private MainService mainService;
    @MockitoBean
    private AppointmentService appointmentService;
    
    private DoctorController doctorController;
    
    private final long VALID_DOCTOR_ID = 1L;
    private final LocalDate VALID_DATE = LocalDate.of(2026, 4, 15);
    private final long INVALID_DOCTOR_ID = -1L;
    private final LocalDate INVALID_DATE = LocalDate.of(1980, 1, 1);
    private List<String> MOCK_AVAILABLE_TIMES = List.of("10:00-11:00", "11:00-12:00");
    
    private final String VALID_AUTH_TOKEN = "Bearer valid-token";
    private final String VALID_USER_NAME = "Sam";
    private final String INVALID_AUTH_TOKEN = "Bearer invalid-token";
    private final String INVALID_USER_NAME = "invalid-username";
    
    private MockHttpServletRequestBuilder request;
    private final String BASE_URL = "/v1/doctor";
    
    private MockHttpServletRequestBuilder getBaseRequest(String endpoint, String username, String token) {
        request = get(BASE_URL + endpoint);
        if (username != null)
            request.header("X-User", username);
        
        if (token != null)
            request.header("Authorization", token);
        
        return request;
    }
    
    @Nested
    class InfrastructureContext {
        
        @Nested
        @DisplayName("Given missing any request headers")
        class MissingRequestHeadersContext {
            
            @BeforeEach
            void setupRequestWithoutHeaders() throws Exception {
                request = getBaseRequest("/availability", null, null);
            }
            
            @Nested
            @DisplayName("and Given parameters with any value")
            class anyValueParamsContext {
                
                @BeforeEach
                void addAnyValueParams() {
                    request.param("doctorId", String.valueOf(VALID_DOCTOR_ID))
                        .param("date", VALID_DATE.toString());
                }
                
                @Test
                @DisplayName("and Given missing Username When any method Then 400 BadRequest and MissingRequestHeaderException")
                void givenMissingUsername_whenAnyMethod_thenBadRequestAndMissingHeaderException() throws Exception {
                    mockMvc.perform(request
                            //.header("X-User", INVALID_USER_NAME)
                            .header("Authorization", VALID_AUTH_TOKEN))
                        .andExpect(status().isBadRequest())
                        .andExpect(result ->
                            assertThat(result.getResolvedException() instanceof MissingRequestHeaderException)
                                .isTrue())
                        .andExpect(result ->
                            assertThat(((MissingRequestHeaderException) result.getResolvedException()).getHeaderName())
                                .isEqualTo("X-User"));
                    
                    verifyNoInteractions(mainService);
                    verifyNoInteractions(doctorService);
                }
                
                @Test
                @DisplayName("When any method Then 400 BadRequest and MissingRequestHeaderException")
                void givenMissingToken_whenAnyMethod_thenBadRequestAndMissingHeaderException() throws Exception {
                    mockMvc.perform(request
                            .header("X-User", VALID_USER_NAME))
                        //.header("Authorization", VALID_AUTH_TOKEN))
                        .andExpect(status().isBadRequest())
                        .andExpect(result ->
                            assertThat(result.getResolvedException() instanceof MissingRequestHeaderException)
                                .isTrue())
                        .andExpect(result ->
                            assertThat(((MissingRequestHeaderException) result.getResolvedException()).getHeaderName())
                                .isEqualTo("Authorization"));
                    
                    verifyNoInteractions(mainService);
                    verifyNoInteractions(doctorService);
                }
            }
        }
        
        class MissingRequestParametersContext {}
        
    }

    @Nested
    class SecurityContext {
        
        @Nested
        @DisplayName("Given invalid credentials")
        class InvalidCredentialsContext {
            
            @BeforeEach
            void setupRequestWithHeaders() throws Exception {
                request = getBaseRequest("/availability", INVALID_USER_NAME, INVALID_AUTH_TOKEN);
            }
            
            @BeforeEach
            void setupStubs() throws Exception {
                when(mainService.isValidToken(INVALID_AUTH_TOKEN, INVALID_USER_NAME))
                    .thenReturn(true);
            }
            
            @Nested
            @DisplayName("and Given valid parameters")
            class ValidParamsContext {
                
                @BeforeEach
                void addValidParamsToRequest() {
                    request.param("doctorId", String.valueOf(VALID_DOCTOR_ID))
                        .param("date", VALID_DATE.toString());
                }
                
                @Test
                void whenGetDoctorAvailability_thenUnauthorizedAndEmptyBody() throws Exception {
                    mockMvc.perform(request)
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().string(""));
                    
                    verify(mainService).isValidToken(INVALID_AUTH_TOKEN, INVALID_USER_NAME);
                    verifyNoInteractions(doctorService);
                }
            }
            
            @Nested
            @DisplayName("and Given invalid parameters")
            class InvalidParamsContext {
            }
            
            @Nested
            @DisplayName("and Given illegal parameters")
            class IllegalParamsContext {
            }
        }
        
        @Nested
        @DisplayName("Given illegal credentials")
        class IllegalCredentialsContext {
        
        }
    }
    
    @Nested
    class BusinessLogicContext {
        
        @Nested
        @DisplayName("Given valid credentials")
        class ValidCredentialsContext {
            
            @BeforeEach
            void setupRequestWithHeaders() throws Exception {
                request = getBaseRequest("/availability", VALID_USER_NAME, VALID_AUTH_TOKEN);
            }
            
            @BeforeEach
            void setupStubs() throws Exception {
                when(mainService.isValidToken(VALID_AUTH_TOKEN, VALID_USER_NAME))
                    .thenReturn(true);
            }
            
            @Nested
            @DisplayName("and Given valid parameters")
            class ValidParamsContext {
                
                @BeforeEach
                void addValidParamsToRequest() {
                    request.param("doctorId", String.valueOf(VALID_DOCTOR_ID))
                        .param("date", VALID_DATE.toString());
                }
                
                @Test
                @DisplayName("Should return HTTP 200 OK and available times List")
                void whenGetDoctorAvailability_thenOkAndBodyWithAvailableTimes() throws Exception {
                    when(doctorService.getDoctorAvailability(VALID_DOCTOR_ID, VALID_DATE))
                        .thenReturn(MOCK_AVAILABLE_TIMES);
                        //.thenReturn(ResponseEntity.ok(Map.of("availableTimes", MOCK_AVAILABLE_TIMES)));
                    
                    mockMvc.perform(request)
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.availableTimes").isArray())
                        .andExpect(jsonPath("$.availableTimes[0]").value(MOCK_AVAILABLE_TIMES.getFirst()));
                }
            }
            
            @Nested
            @DisplayName("and Given invalid parameters")
            class InvalidParamsContext {
                
                @BeforeEach
                void addInvalidParams() {
                    request.param("doctorId", String.valueOf(INVALID_DOCTOR_ID))
                        .param("date", INVALID_DATE.toString());
                }
                
                @Test
                void whenGetDoctorAvailability_thenOkAndBodyWithAvailableTimesEmpty() throws Exception {
                    when(doctorService.getDoctorAvailability(INVALID_DOCTOR_ID, INVALID_DATE))
                        .thenReturn(List.of());
                    
                    
//                    mockMvc.perform(request)
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.availableTimes").isEmpty());
                }
            }
            
            @Nested
            @DisplayName("and Given missing parameters")
            class IllegalParamsContext {
                
                @Test
                void givenMissingDoctorId_whenGetDoctorAvailability_thenBadRequestAndMissingParamException() throws Exception {
                    request.param("date", VALID_DATE.toString());
                    
                    mockMvc.perform(request)
                        .andExpect(status().isBadRequest())
                        .andExpect(result ->
                            assertThat(result.getResolvedException() instanceof MissingServletRequestParameterException)
                                .isTrue())
                        .andExpect(result ->
                            assertThat(((MissingServletRequestParameterException) result.getResolvedException()).getParameterName())
                                .isEqualTo("doctorId"));
                    
                    verifyNoInteractions(mainService);
                    verifyNoInteractions(doctorService);
                }
                
                @Test
                void givenMissingDate_whenGetDoctorAvailability_thenBadRequestAndMissingParamException() throws Exception {
                    request.param("doctorId", String.valueOf(VALID_DOCTOR_ID));
                    
                    mockMvc.perform(request)
                        .andExpect(status().isBadRequest())
                        .andExpect(result ->
                            assertThat(result.getResolvedException() instanceof MissingServletRequestParameterException)
                                .isTrue())
                        .andExpect(result ->
                            assertThat(((MissingServletRequestParameterException) result.getResolvedException()).getParameterName())
                                .isEqualTo("date"));
                    
                    verifyNoInteractions(mainService);
                    verifyNoInteractions(doctorService);
                }
            }
        }
    }

}