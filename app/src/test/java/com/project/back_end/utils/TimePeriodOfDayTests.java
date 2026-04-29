package com.project.back_end.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.project.back_end.utils.TimePeriodOfDay.AM;
import static com.project.back_end.utils.TimePeriodOfDay.PM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimePeriodOfDayTests {
    private final String availableTimeAsAM = "10:00-11:00";
    private final String availableTimeAsPM = "12:00-13:00";
    // TODO: 1. ¿Es buena idea el asignar a una constante el Patrón Blank para violaciones de restricciones de Test Paramétrizados?
    //       1.1. Si: Hay un patrón mejor para probar "blank".
    private final String blankPattern = " \t\n\r\f";
    
    @Nested // TODO: 2. ¿Un nombre más legible y a la vez descriptivo para la clase del test?
    class TimePeriodOfDaySuccessfulTests {
        @Test // TODO: 3. ¿Un nombre más legible y a la vez descriptivo para las funciones de los test?
        void givenAMAndAvailableTimeAsAM_whenIsAtThisTimeOfDay_thenIsTrue() throws Exception {
            assertThat(AM.isAtThisTimeOfDay(availableTimeAsAM)).isTrue();
        }
        
        @Test
        void givenPMAndAvailableTimeAsPM_whenIsAtThisTimeOfDay_thenIsTrue() throws Exception {
            assertThat(PM.isAtThisTimeOfDay(availableTimeAsPM)).isTrue();
        }
    }
    
    @Nested
    class TimePeriodOfDayFailTests {
        
        @Test
        void givenAMAndAvailableTimeAsPM_whenIsAtThisTimeOfDay_thenIsFalse() throws Exception {
            assertThat(AM.isAtThisTimeOfDay(availableTimeAsPM)).isFalse();
        }
        
        @Test
        void givenPMAndAvailableTimeAsAM_whenPMIsThisAtTimeOfDay_thenIsFalse() throws Exception {
            assertThat(PM.isAtThisTimeOfDay(availableTimeAsAM)).isFalse();
        }
    }
    
    @Nested
    class TimePeriodOfDayErrorTests {
        // TODO: 4. ¿Más comprobaciones de Error?
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = blankPattern)
        void givenAMAndAvailableTimeAsPM_whenIsAtThisTimeOfDay_thenIsFalse(String invalidTime) throws Exception {
            assertThrows(IllegalArgumentException.class, () -> AM.isAtThisTimeOfDay(invalidTime));
            assertThrows(IllegalArgumentException.class, () -> PM.isAtThisTimeOfDay(invalidTime));
        }
    }
}
