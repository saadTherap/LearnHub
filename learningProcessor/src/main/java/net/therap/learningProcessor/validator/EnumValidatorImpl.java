package net.therap.learningProcessor.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.therap.learningProcessor.annotation.EnumValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author avidewan
 * @since 8/3/25
 */
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValidator annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Objects.nonNull(value) && acceptedValues.contains(value.toUpperCase());
    }
}