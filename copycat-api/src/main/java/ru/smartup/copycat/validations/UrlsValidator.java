package ru.smartup.copycat.validations;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class UrlsValidator extends BaseValidator implements ConstraintValidator<URLs, List<String>> {
    @Override
    public boolean isValid(List<String> urls, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (urls == null || urls.size() == 0) {
            customMessageForValidation(context, "url list is empty");
            return false;
        }

        for (String url : urls) {
            if (!url.matches("^(https?|http?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                customMessageForValidation(context, "starting point does not match url pattern");
                return false;
            }
        }

        return true;
    }


}
