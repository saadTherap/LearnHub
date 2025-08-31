package net.therap.app.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.therap.app.validation.validator.UniqueEmailValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author gazizafor
 * @since 13/8/25
 */

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface UniqueEmail {
    
    String message() default "{validation.email.unique}";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}