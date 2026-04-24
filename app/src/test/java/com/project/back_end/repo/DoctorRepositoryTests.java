package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import com.project.back_end.utils.ConstraintViolationField;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import static com.project.back_end.config.EntityConstraintsConfig.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/smartcare_coursera"
})
public class DoctorRepositoryTests {
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    public TestEntityManager testEntityManager;
    private Doctor expectedDoc;
    
    /*
        @BeforeAll
        public static void setUpValidator() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }
     */
    /*
        ## TODO: Podemos hacer Pruebas Unitarias a los Modelos utilizando Jakarta Validator, sin necesidad de ampliar
        el alcance a Pruebas de Integración con el Repositorio.
        
        2.2.2.1. Validator#validateDoctor()
        Use the validateDoctor() method to perform validation of all constraints of a given bean.
        Example 2.14, “Using Validator#validateDoctor()” shows the validation of an instance of the Car class
        from Example 2.2, “Property-level constraints” which fails to satisfy the @NotNull constraint on
        the manufacturer property. The validation call therefore returns one ConstraintViolation object.
        · info: https://docs.hibernate.org/validator/8.0/reference/en-US/html_single/#section-validating-bean-constraints#_validatorvalidate
     */
    @BeforeEach
    void setup() {
        this.expectedDoc = new Doctor.Builder()
            .name("Test Name 1")
            .specialty("Test Specialty 1")
            .email("test1@email.com")
            .password("Password123")
            .phone("1231231234")
            .availableTimes(List.of("10:00 - 11:00", "11:00 - 12:00", "14:00 - 15:00"))
            .yearsOfExperience(5)
            .clinicAddress("Test Clinic Address 1")
            .rating(4.0).build();
    }
    
    private void assertConstraintViolFieldMatchesConstraintViolException(
        ConstraintViolationException constraintViolEx, ConstraintViolationField field) throws Exception {
        
        ConstraintViolation<?> foundConstraintViol =
            constraintViolEx
                .getConstraintViolations()
                .stream()
                .filter(cv ->
                    cv.getPropertyPath().toString().equals(field.getFieldName()) &&
                    cv.getConstraintDescriptor().getAnnotation().annotationType().equals(field.getConstraintAnnotation()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                    "Constraint Violation: " + field.getConstraintAnnotation().getName() +
                    ", not found for field: " + field.getFieldName()));
        
        assertThat(foundConstraintViol.getMessage()).isEqualTo(field.getConstraintViolMsg());
    }
    
    private void assertConstraintViolFieldsListMatchesConstraintViolException(
        ConstraintViolationException constraintViolEx, List<ConstraintViolationField> fields) throws Exception {
        
        for (ConstraintViolationField field : fields) {
            assertConstraintViolFieldMatchesConstraintViolException(constraintViolEx, field);
        }
    }
    
    /*
     * # 1. Successful Use cases.
     */
    /*
     * ## 1.1. Registration Successful Use cases.
     */
    @Test
    void givenValidArguments_whenRegister_thenNewDoctorIsRegisteredWithThatArguments() throws Exception {
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        
        assertThat(actualDoc.getName()).isEqualTo(expectedDoc.getName());
        assertThat(actualDoc.getSpecialty()).isEqualTo(expectedDoc.getSpecialty());
        assertThat(actualDoc.getEmail()).isEqualTo(expectedDoc.getEmail());
        assertThat(actualDoc.getPassword()).isEqualTo(expectedDoc.getPassword());
        assertThat(actualDoc.getPhone()).isEqualTo(expectedDoc.getPhone());
        assertIterableEquals(actualDoc.getAvailableTimes(), expectedDoc.getAvailableTimes());
        assertThat(actualDoc.getYearsOfExperience()).isEqualTo(expectedDoc.getYearsOfExperience());
        assertThat(actualDoc.getRating()).isEqualTo(expectedDoc.getRating());
    }
    
    /*
     * ## 1.2. Search Successful Use cases.
     */
    @Test
    void givenAnExistingId_whenFindById_thenReturnThatDoctor() throws Exception {
        Doctor persistedDoc = testEntityManager.persistFlushFind(expectedDoc);
        doctorRepo.findById(persistedDoc.getId()).orElseThrow();
    }
    
    /*
     * # 2. Edge Use cases.
     */
    /*
     * ## 2.1. Registration Edge Use cases.
     * Perform tests for every field constraint with input values being at the edge of acceptance for Doctor's model.
     *
     * TODO: Enhance edge valid inputs by trimming head and tail of input Strings.
     * TODO: Enhance edge valid inputs for Regex Constraint test for fields: Email, Password Simple and Phone.
     * - Check thoroughly "a@-.cc", it fails but it should not.
     * - Check thoroughly "0aA   ", it passes but it should not. We need to trim ahead and tail.
     * TODO: Revise why getAvailableTimes().getFirst() -> throws error: java: cannot find symbol method getFirst()
     */
    @ParameterizedTest
    @ValueSource(ints = { NAME_SIZE_MIN, NAME_SIZE_MAX })
    void givenNameIsAtTheEdgeOfSizeConstraint_whenRegister_thenRegisteredDoctorIsReturn(int validSize) throws Exception {
        expectedDoc.setName(" ".repeat(validSize - 1) + "a");
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getName()).isEqualTo(expectedDoc.getName());
    }

    @ParameterizedTest
    @ValueSource(ints = { SPECIALTY_SIZE_MIN, SPECIALTY_SIZE_MAX })
    void givenSpecialtyIsAtTheEdgeOfSizeConstrain_whenRegister_thenSuccessAndNewDoctorIdIsReturn(int validSize) throws Exception {
        expectedDoc.setSpecialty(" ".repeat(validSize - 1) + "a");
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getSpecialty()).isEqualTo(expectedDoc.getSpecialty());
    }
    
    @ParameterizedTest
    @ValueSource(strings = { "a@b.cc", "A@b.cc", "0@b.cc", "_@b.cc", "+@b.cc", "-@b.cc", "*@b.cc", "&@b.cc",
        "a.a@b.cc", "A.a@b.cc", "0.a@b.cc", "_.a@b.cc", "+.a@b.cc", "-.a@b.cc", "*.a@b.cc", "&.a@b.cc",
        "a.A@b.cc", "a.0@b.cc", "a._@b.cc", "a.+@bb.cc", "a.-@b.cc", "a.*@b.cc", "a.&@b.cc", "a@B.cc", "a@0.cc",
        "a@b.Cc", "a@b.cC", "a@b.CC" })
    void givenEmailIsAtTheEdgeOfEmailConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(String validSyntax) throws Exception {
        expectedDoc.setEmail(validSyntax);
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getEmail()).isEqualTo(expectedDoc.getEmail());
    }
    
    @ParameterizedTest
    @ValueSource(strings = { "0aA   ", "0aA_*&", "0123aA", "0aABCD", "0abcdA", "aA012345678901234567",
        "0aAaaaaaaaaaaaaaaaaa", "0aABCDEFGHIJKLMNÑOPQ", "0aA_.:-*+^`'{(=¿?!@$" })
    void givenPasswordIsAtTheEdgeOfPatternConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(String validSyntax) throws Exception {
        expectedDoc.setPassword(validSyntax);
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getPassword()).isEqualTo(expectedDoc.getPassword());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"0000000000", "0123456789" })
    void givenPhoneIsAtTheEdgeOfPatternConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(String validSyntax) throws Exception {
        expectedDoc.setPhone(validSyntax);
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getPhone()).isEqualTo(expectedDoc.getPhone());
    }
    
    @Test
    void givenAvailableTimesIsAtTheEdgeOfNotEmptyConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn() throws Exception {
        expectedDoc.setAvailableTimes(List.of("09:00 - 10:00"));
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);;
        assertThat(actualDoc.getAvailableTimes().size()).isEqualTo(1);
        assertThat(actualDoc.getAvailableTimes().get(0)).isEqualTo(expectedDoc.getAvailableTimes().get(0));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { YEARS_OF_EXP_RANGE_MIN, YEARS_OF_EXP_RANGE_MAX })
    void givenYearsOfExperienceIsAtTheEdgeOfRangeConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(int validRange) throws Exception {
        expectedDoc.setYearsOfExperience(validRange);
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getYearsOfExperience()).isEqualTo(expectedDoc.getYearsOfExperience());
    }

    @ParameterizedTest
    @ValueSource(ints = { ADDRESS_SIZE_MIN, ADDRESS_SIZE_MAX })
    void givenClinicAddressIsAtTheEdgeOfSizeConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(int validSize) throws Exception {
        expectedDoc.setClinicAddress("a".repeat(validSize));
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getClinicAddress()).isEqualTo(expectedDoc.getClinicAddress());
    }
    
    @ParameterizedTest
    @ValueSource(ints = { RATING_RANGE_MIN, RATING_RANGE_MAX })
    void givenRatingIsAtTheEdgeOfRangeConstraint_whenRegister_thenSuccessAndNewDoctorIdIsReturn(int validRange) throws Exception {
        expectedDoc.setRating(validRange);
        Doctor actualDoc = testEntityManager.persistFlushFind(expectedDoc);
        assertThat(actualDoc.getRating()).isEqualTo(expectedDoc.getRating());
    }
    
    /*
     * # 3. Error Use cases tests.
     *
     */
    /*
     * ## 3.1. Registration Error Use cases.
     * Perform unit tests for every field constraint with input values failing validation rules of Doctor's model.
     * Also a composite final test of a Doctor's Registration populated with invalid values for all fields.
     *
     * TODO: Enhance edge valid inputs by trimming head and tail of input Strings.
     * TODO: Enhance edge valid inputs for Regex Constraint test for fields: Email, Password Simple and Phone.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenNameViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankNameException(String invalidInstantiation) throws Exception {
        expectedDoc.setName(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(constraintViolEx,
            new ConstraintViolationField("name", NotBlank.class, NAME_NOT_BLANK_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { NAME_SIZE_MIN - 1, NAME_SIZE_MAX + 1 })
    void givenNameViolatesSizeConstraint_whenRegister_thenThrowsInvalidSizeNameException(int invalidSize) throws Exception {
        expectedDoc.setName("a".repeat(invalidSize));
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("name", Size.class, NAME_INVALID_SIZE_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenSpecialtyViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankSpecialtyException(String invalidInstantiation) throws Exception {
        expectedDoc.setSpecialty(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("specialty", NotBlank.class, SPECIALTY_NOT_BLANK_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { SPECIALTY_SIZE_MIN - 1, SPECIALTY_SIZE_MAX + 1 })
    void givenSpecialtyViolatesSizeConstraint_whenRegister_thenThrowsInvalidSizeSpecialtyException(int invalidSize) throws Exception {
        expectedDoc.setSpecialty("a".repeat(invalidSize));
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("specialty", Size.class, SPECIALTY_INVALID_SIZE_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenEmailViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankEmailException(String invalidInstantiation) throws Exception {
        expectedDoc.setEmail(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("email", NotBlank.class, EMAIL_NOT_BLANK_MSG));
    }
    
    @Test
    void givenAnEmailExists_whenRegister_thenThrowsNonUniqueEmailException() throws Exception {
        String alreadyExistsEmail = doctorRepo.findById(1L).orElseThrow().getEmail();
        expectedDoc.setEmail(alreadyExistsEmail);
        DataIntegrityViolationException dive =
            assertThrowsExactly(
                DataIntegrityViolationException.class,
                () -> doctorRepo.register(expectedDoc));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"doctor#gmail.com", "doctor@", "@gmail.com", "doctor@gmailcom"})
    void givenEmailViolatesPatternConstraint_whenRegister_thenThrowsInvalidPatternEmailException(String invalidSyntax) throws Exception {
        expectedDoc.setEmail(invalidSyntax);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("email", Email.class, EMAIL_INVALID_REGEX_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenPasswordViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankPasswordException(String invalidInstantiation) throws Exception {
        expectedDoc.setPassword(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("password", NotBlank.class, PASSWORD_SIMPLE_NOT_BLANK_MSG));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abcdef", "ABCDEF", "02345", "abc012", "012abc", "ABC012", "012ABC", "aB012", "aB0__0123456789012345" })
    void givenPasswordViolatesPatternConstraint_whenRegister_thenThrowsInvalidPatternPasswordException(String invalidSyntax) throws Exception {
        expectedDoc.setPassword(invalidSyntax);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("password", Pattern.class, PASSWORD_SIMPLE_INVALID_PATTERN_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenPhoneViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankPhoneException(String invalidInstantiation) throws Exception {
        expectedDoc.setPhone(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("phone", NotBlank.class, PHONE_NOT_BLANK_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"a012345678", "abcdefghi1", "012345678", "01234567890"})
    void givenPhoneViolatesPatternConstraint_whenRegister_thenThrowsInvalidPatternPhoneException(String invalidSyntax) throws Exception {
        expectedDoc.setPhone(invalidSyntax);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("phone", Pattern.class, PHONE_INVALID_PATTERN_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void givenAvailableTimesViolatesNotEmptyConstraint_whenRegister_thenThrowsNotEmptyAvailableTimesException(List<String> invalidInstantiation) throws Exception {
        expectedDoc.setAvailableTimes(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("availableTimes", NotEmpty.class, AVAILABLE_TIMES_NOT_EMPTY_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { YEARS_OF_EXP_RANGE_MIN - 1, YEARS_OF_EXP_RANGE_MAX + 1})
    void givenYearsOfExpViolatesRangeConstraint_whenRegister_thenThrowsInvalidRangeYearsOfExperienceException(int invalidRange) throws Exception {
        expectedDoc.setYearsOfExperience(invalidRange);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("yearsOfExperience", Range.class, YEARS_OF_EXP_INVALID_RANGE_MSG));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t\n\r\f"})
    void givenClinicAddressViolatesNotBlankConstraint_whenRegister_thenThrowsNotBlankClinicAddressException(String invalidInstantiation) throws Exception {
        expectedDoc.setClinicAddress(invalidInstantiation);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("clinicAddress", NotBlank.class, ADDRESS_NOT_BLANK_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(ints = { ADDRESS_SIZE_MIN - 1, ADDRESS_SIZE_MAX + 1})
    void givenClinicAddressViolatesSizeConstraint_whenRegister_thenThrowsInvalidSizeClinicAddressException(int invalidSize) throws Exception {
        expectedDoc.setClinicAddress("1".repeat(invalidSize));
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class, () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("clinicAddress", Size.class, ADDRESS_INVALID_SIZE_MSG));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = { RATING_RANGE_MIN - 0.1, RATING_RANGE_MAX + 0.1})
    void givenRatingViolatesRangeConstraint_whenRegister_thenThrowsInvalidRangeRatingException(double invalidRange) throws Exception {
        expectedDoc.setRating(invalidRange);
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldMatchesConstraintViolException(
            constraintViolEx,
            new ConstraintViolationField("rating", Range.class, RATING_INVALID_RANGE_MSG));
    }
    
    @Test
    void givenManyArgumentsViolatesConstraints_whenRegister_thenThrowsThatManyFieldsConstraintsExceptions() throws Exception {
        expectedDoc.setName("a");
        expectedDoc.setSpecialty("a");
        expectedDoc.setEmail("a#a");
        expectedDoc.setPassword("a");
        expectedDoc.setPhone("0");
        expectedDoc.setAvailableTimes(Collections.emptyList());
        expectedDoc.setYearsOfExperience(-1);
        expectedDoc.setClinicAddress("a");
        expectedDoc.setRating(-1);
        
        List<ConstraintViolationField> invalidFields =
            List.of(
                new ConstraintViolationField("name", Size.class, NAME_INVALID_SIZE_MSG),
                new ConstraintViolationField("specialty", Size.class, SPECIALTY_INVALID_SIZE_MSG),
                new ConstraintViolationField("email", Email.class, EMAIL_INVALID_REGEX_MSG),
                new ConstraintViolationField("password", Pattern.class, PASSWORD_SIMPLE_INVALID_PATTERN_MSG),
                new ConstraintViolationField("phone", Pattern.class, PHONE_INVALID_PATTERN_MSG),
                new ConstraintViolationField("availableTimes", NotEmpty.class, AVAILABLE_TIMES_NOT_EMPTY_MSG),
                new ConstraintViolationField("yearsOfExperience", Range.class, YEARS_OF_EXP_INVALID_RANGE_MSG),
                new ConstraintViolationField("clinicAddress", Size.class, ADDRESS_INVALID_SIZE_MSG),
                new ConstraintViolationField("rating", Range.class, RATING_INVALID_RANGE_MSG));
        
        ConstraintViolationException constraintViolEx =
            assertThrowsExactly(
                ConstraintViolationException.class,
                () -> testEntityManager.persistAndFlush(expectedDoc));
        assertConstraintViolFieldsListMatchesConstraintViolException(constraintViolEx, invalidFields);
    }
    
    /*
     * ## 3.2. Searching Error Use cases.
     * Perform unit tests for every field constraint involved on Searching methods from Doctor's Repository,
     * with input values failing validation rules of Doctor's model.
     *
     */
    
    // ID != null (Error)
    // ID <= 0 , id > MAX_LONG // Range Constraint
    // ID != double. // Type Constraint
    
//    @ParameterizedTest
//    @NullAndEmptySource
//    @ValueSource(" \t\n\r\f")
//    void givenIdViolatesNotBlankRequirement_whenFindById_thenThrowsNotBlankIdException(String invalidInstantiation) throws Exception {
//        // expectedDoc.setId(validRange); // We cannot set directly the Doctor's ID when using persistance creation.
//        // Doctor actualDoc = persistByTestEntityManager(expectedDoc).orElseThrow();;
//        Optional<Doctor> actualDoc = doctorRepo.findById(invalidInstantiation);
//
//        assertThat(actualDoc.isPresent())
//            .withFailMessage("Doctor MainService method FindById fail with Id in the edge of its range values: " + invalidInstantiation)
//            .isTrue();
//        assertThat(actualDoc.get().getId()).isEqualTo(expectedDoc.getId());
//    }
    
    
    //void givenIdOutOfIdRange_whenFindByID_thenThrowsOutOfRangeIdException(long invalidRange) throws Exception {
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
