package ru.smartup.copycat.validations;

import javax.validation.ConstraintValidatorContext;

public abstract class BaseValidator {
    protected void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
