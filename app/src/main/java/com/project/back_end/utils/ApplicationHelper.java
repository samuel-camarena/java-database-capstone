package com.project.back_end.utils;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationHelper {
    
    //@Value("${email-validation-regex}")
    public static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    
    // OWASP Simple Password Validation Regex
    // - 4 to 8 character password requiring numbers and both lowercase and uppercase letters
    //@Value("${password-simple-validation-regex}")
    public static final String PASSWORD_SIMPLE_VALIDATION_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$";
    
    // OWASP Complex Password Validation Regex
    // - 4 to 8 character password requiring numbers and both lowercase and uppercase letters
    //@Value("${password-complex-validation-regex}")
    public static final String PASSWORD_COMPLEX_VALIDATION_REGEX
        = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])" +
        "(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})" +
        "[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{12,128}$";
    
}
