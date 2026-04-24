package com.project.back_end.utils;

import java.lang.annotation.Annotation;

/**
 *   Class helper to check which Constraint Violations had occur to by which entity field.
 *   - Posible nombre: private class InvalidEntityField
 */
public class ConstraintViolationField {
    private String fieldName;
    private Class<? extends Annotation> constraintAnnotation;
    private String constraintViolMsg;
    
    public ConstraintViolationField(String fieldName, Class<? extends Annotation> constraintAnnotation,
                                    String constraintViolMsg) {
        this.fieldName = fieldName;
        this.constraintAnnotation = constraintAnnotation;
        this.constraintViolMsg = constraintViolMsg;
    }
    
    public ConstraintViolationField(Builder builder) {
        this.fieldName = builder.fieldName;
        this.constraintAnnotation = builder.constraintAnnotation;
        this.constraintViolMsg = builder.constraintViolMsg;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Class<? extends Annotation> getConstraintAnnotation() {
        return constraintAnnotation;
    }
    
    public String getConstraintViolMsg() {
        return constraintViolMsg;
    }
    
    public static class Builder {
        private String fieldName;
        private Class<? extends Annotation> constraintAnnotation;
        private String constraintViolMsg;
        
        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        public Builder constraintAnnotation(Class<? extends Annotation> constraintAnnotation) {
            this.constraintAnnotation = constraintAnnotation;
            return this;
        }
        
        public Builder constraintViolMsg(String constraintViolMsg) {
            this.constraintViolMsg = constraintViolMsg;
            return this;
        }
        
        public ConstraintViolationField build() {
            return new ConstraintViolationField(this);
        }
    }
}
