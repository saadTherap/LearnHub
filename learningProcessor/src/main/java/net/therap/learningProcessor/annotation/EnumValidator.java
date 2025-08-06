package net.therap.learningProcessor.annotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.therap.learningProcessor.validator.EnumValidatorImpl;

import java.lang.annotation.*;

/**
 * @author avidewan
 * @since 8/3/25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
@Documented
public @interface EnumValidator {
    String message() default "Value is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}
