package com.project.back_end;

import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackEndApplicationTests {
    
    @Value("${email-validation-regex}")
    private String emailValidationRegex;
    @Value("${password-simple-validation-regex}")
    private String passwordSimpleValidationRegex;
    @Autowired
    ApplicationContext appContext;
    
	@Test
	void contextLoads() {}
    
    @Test
    void checkSomeBeansCreations() {
        Map<String, DoctorService> doctorServiceV2BeanMap = appContext.getBeansOfType(DoctorService.class);
        Map<String, AppointmentService> appointmentServiceBeanMap = appContext.getBeansOfType(AppointmentService.class);
        Map<String, AppointmentRepository> appointmentRepoBeanMap = appContext.getBeansOfType(AppointmentRepository.class);
        
        assertThat(doctorServiceV2BeanMap).isNotEmpty();
        assertThat(doctorServiceV2BeanMap.get("doctorServiceV2")).isNotNull();
        assertThat(appointmentServiceBeanMap).isNotEmpty();
        assertThat(appointmentServiceBeanMap.get("appointmentService")).isNotNull();
        assertThat(appointmentRepoBeanMap).isNotEmpty();
        assertThat(appointmentRepoBeanMap.get("appointmentRepository")).isNotNull();
    }
    
    @Test
    void givenARegexExistsAsAppProperty_whenInjected_thenIsAvailable() throws Exception {
        assertThat(emailValidationRegex)
            .isEqualTo("^[a-zA-Z0-9_+&*-]+(?:.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,}$");
        assertThat(passwordSimpleValidationRegex)
            .isEqualTo("^(?=.*d)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])..{6,20}$");
    }

}
