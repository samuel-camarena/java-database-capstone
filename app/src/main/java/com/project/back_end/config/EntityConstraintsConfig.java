package com.project.back_end.config;

public class EntityConstraintsConfig {
    // # Standardized MSGs for Constraint Violations.
    public static final String NOT_NULL_MSG                         = "must not be null";
    public static final String NOT_EMPTY_MSG                        = "must not be null nor empty";
    public static final String NOT_BLANK_MSG                        = "must not be null nor blank";
    //public static final String NOT_BLANK_MSG_ES                   = "no debe ser nulo o vacío";
    public static final String NON_UNIQUE_MSG                        = "must be unique";
    public static final String INVALID_SIZE_MSG                     = "must be between {min} and {max} characters";
    public static final String DATE_TIME_AT_FUTURE_MSG           = "date and time must be in the future";
    
    // # Generic Fiel Constraints:
    public static final String NAME_NOT_BLANK_MSG                   = "Name " + NOT_BLANK_MSG;
    //public static final String NAME_NOT_BLANK_MSG_ES              = "Nombre " + NOT_BLANK_MSG_ES;
    public static final int NAME_SIZE_MIN                           = 3;
    public static final int NAME_SIZE_MAX                           = 100;
    //public static final String NAME_INVALID_SIZE_MSG              = "Name must be between {min} and {max} characters";
    public static final String NAME_INVALID_SIZE_MSG                = "Name must be between " + NAME_SIZE_MIN + " and " + NAME_SIZE_MAX + " characters";
    
    public static final String EMAIL_NOT_BLANK_MSG                  = "Email " + NOT_BLANK_MSG;
    public static final String EMAIL_NON_UNIQUE_MSG                 = "Email " + NON_UNIQUE_MSG;
    public static final String EMAIL_VALIDATION_REGEX               = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    public static final String EMAIL_INVALID_REGEX_MSG              = "Email address must have a valid format";
    
    public static final String PASSWORD_SIMPLE_NOT_BLANK_MSG        = "Password " + NOT_BLANK_MSG;
    /*
      OWASP Simple Password Validation Regex with custom extension:
      - 6 to 20 character password requiring numbers and both lowercase and uppercase letters
     */
    //@Value("${password-simple-validation-regex}")
    public static final String PASSWORD_SIMPLE_VALIDATION_REGEX     = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20}$";
    public static final String PASSWORD_SIMPLE_INVALID_PATTERN_MSG  = "Password must be 6-20 characters, containing any combination of characters having minimum a lowercase plus an uppercase letters and any number";
    /*
        OWASP Complex Password Validation Regex
    */
    public static final String PASSWORD_COMPLEX_VALIDATION_REGEX    =
        "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])" +
        "(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})" +
        "[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{12,128}$";
    
    public static final String PHONE_NOT_BLANK_MSG                  = "Phone " + NOT_BLANK_MSG;
    public static final String PHONE_VALIDATION_REGEX               = "^[0-9]{10}$";
    public static final String PHONE_INVALID_PATTERN_MSG            = "Phone number must be 10 digits";
    
    public static final String ADDRESS_NOT_BLANK_MSG                = "Address " + NOT_BLANK_MSG;
    public static final int ADDRESS_SIZE_MIN                        = 5;
    public static final int ADDRESS_SIZE_MAX                        = 255;
    public static final String ADDRESS_INVALID_SIZE_MSG             = "Address must be between " + ADDRESS_SIZE_MIN + " and " + ADDRESS_SIZE_MAX + " characters";
    
    public static final String APPOINTMENT_NOT_NULL_MSG             = "Appointment " + NOT_NULL_MSG;
    public static final String APPOINTMENT_DATE_TIME_AT_FUTURE_MSG  = "Appointment " + DATE_TIME_AT_FUTURE_MSG;
    
    // # Doctor's Field Constraints:
    
    public static final String SPECIALTY_NOT_BLANK_MSG              = "Specialty " + NOT_BLANK_MSG;
    public static final int SPECIALTY_SIZE_MIN                      = 3;
    public static final int SPECIALTY_SIZE_MAX                      = 50;
    public static final String SPECIALTY_INVALID_SIZE_MSG           = "Specialty must be between " + SPECIALTY_SIZE_MIN + " and " + SPECIALTY_SIZE_MAX + " characters";
    
    public static final String AVAILABLE_TIMES_NOT_EMPTY_MSG        = "Available times " + NOT_EMPTY_MSG;
    
    public static final String YEARS_OF_EXP_NOT_NULL_MSG            = "Years of experience " + NOT_NULL_MSG;
    public static final int YEARS_OF_EXP_RANGE_MIN                  = 0;
    public static final int YEARS_OF_EXP_RANGE_MAX                  = 99;
    public static final String YEARS_OF_EXP_INVALID_RANGE_MSG       = "Years of experience must be between " + YEARS_OF_EXP_RANGE_MIN + " and " + YEARS_OF_EXP_RANGE_MAX + " years";
    
    public static final String RATING_NOT_NULL_MSG                  = "Rating " + NOT_EMPTY_MSG;
    public static final int RATING_RANGE_MIN                        = 1;
    public static final int RATING_RANGE_MAX                        = 5;
    public static final String RATING_INVALID_RANGE_MSG             = "Rating must be between " + RATING_RANGE_MIN + " and " + RATING_RANGE_MAX + " points";
    
    // ## Doctor's MainService and Internal Error Messages
    public static final String DOCTOR_NOT_NULL_MSG                  = "Doctor " + NOT_NULL_MSG;
    public static final String DOCTOR_NOT_EMPTY_MSG                 = "Doctor " + NOT_EMPTY_MSG;
    public static final String DOCTORS_NOT_NULL_MSG                 = "Doctors list " + NOT_NULL_MSG;
    public static final String DOCTORS_NOT_EMPTY_MSG                = "Doctors list " + NOT_EMPTY_MSG;
    public static final String DOCTOR_ID_NOT_EXISTS_MSG             = "No Doctor registered by ID: ";
    public static final String DOCTOR_EMAIL_NOT_EXISTS_MSG          = "No Doctor registered by email: ";
    public static final String DOCTOR_NON_UNIQUE_EMAIL_MSG          = "Doctor " + EMAIL_NON_UNIQUE_MSG;
    public static final String DOCTOR_NON_UNIQUE_EMAIL_FRIENDLY_MSG = "Doctor's email already registered";
    public static final String TIME_PERIOD_NOT_BLANK_MSG            = "Time period must not be null nor blank";
    public static final String DOCTORS_NOT_FOUND_MSG                = "Doctors not found";
    
    
    // # Patient's Field Constraints:
    public static final String PATIENT_NOT_NULL_MSG                 = "Patient " + NOT_NULL_MSG;
    
    public static final String DATE_OF_BIRTH_NOT_NULL_MSG           = "Date of birth " + NOT_NULL_MSG;
    public static final String DATE_OF_BIRTH_NOT_PAST_MSG           = "Date of birth must be in past";
    
    public static final String EMERGENCY_CONTACT_NOT_BLANK_MSG      = "Emergency contact " + NOT_BLANK_MSG;
    
    public static final String INSURANCE_PROVIDER_NOT_BLANK_MSG     = "Insurance provider " + NOT_BLANK_MSG;
    public static final int INSURANCE_PROVIDER_SIZE_MIN             = 3;
    public static final int INSURANCE_PROVIDER_SIZE_MAX             = 100;
    public static final String INSURANCE_PROVIDER_INVALID_SIZE      = "Insurance provider must be between " + INSURANCE_PROVIDER_SIZE_MIN + " and " + INSURANCE_PROVIDER_SIZE_MAX + " characters";
    
    // # Appointment's Field Constraints:
    public static final String STATUS_NOT_NULL_MSG                  = "Status " + NOT_NULL_MSG;
    public static final String STATUS_INVALID_RANGE_MSG             = "Status must be 0 (scheduled) or 1 (completed)";
    public static final String VISITING_REASON_NOT_BLANK_MSG        = "Reason for visiting " + NOT_BLANK_MSG;
    public static final String VISITING_REASON_INVALID_MAX_MSG      = "Reason for visiting must be maximum 200 characters";
    public static final String NOTES_INVALID_MAX_MSG                = "Notes must be maximum 200 characters";
    
    // # Admin's Field Constraints:
    public static final String USERNAME_NOT_NULL_MSG                = "Username " + NOT_NULL_MSG;
    public static final int USERNAME_SIZE_MIN = 3;
    public static final int USERNAME_SIZE_MAX = 50;
    public static final String USERNAME_INVALID_SIZE_MSG            = "Username must be between " + USERNAME_SIZE_MIN +" and " + USERNAME_SIZE_MAX + " characters";
    
    // # Token MainService
    public static final String TOKEN_INVALID_MSG                    = "Token is invalid or has expired";
}
