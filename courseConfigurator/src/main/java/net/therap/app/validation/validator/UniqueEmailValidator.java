package net.therap.app.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.therap.app.service.InstructorService;
import net.therap.app.validation.annotations.UniqueEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

/**
 * @author gazizafor
 * @since 13/8/25
 */
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private InstructorService instructorService;
    
    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        logger.info("Initializing unique email validator");
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        logger.info("Validating (isUnique) email {}", email);
        
        if (isNull(email) || email.isBlank()) {
            return true;
        }
        
        return !instructorService.isEmailAlreadyInUse(email);
    }
}