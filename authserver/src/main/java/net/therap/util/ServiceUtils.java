package net.therap.util;

import net.therap.entity.User;
import net.therap.enums.UserRole;
import net.therap.exception.UserExistenceException;
import net.therap.respository.UserRepository;

import java.util.Optional;

import static net.therap.util.ErrorMessages.FIND_USER_ERROR;

/**
 * @author apurboturjo
 * @since 7/27/25
 */
public class ServiceUtils {
    
    public static UserRole toSystemFormatUserRole(String role) {
        return UserRole.valueOf(role.toUpperCase());
    }
}