package com.project.back_end;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackEndApplicationTests {
    
    @Value("${email-validation-regex}")
    private String emailValidationRegex;
    
	@Test
	void contextLoads() {
	}
    
    @Test
    void givenEmailValidationPropertyExists_whenItIsInjected_thenPropertyIsAvailable() throws Exception {
        assertThat(emailValidationRegex)
            .isEqualTo("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
    }

}
