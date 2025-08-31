package net.therap.auth.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.auth.server.dto.*;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.service.interfaces.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author apurboturjo
 * @since 8/27/25
 */
@WebMvcTest
@ContextConfiguration(classes = {AuthController.class})
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private AuthService authService;
    
    @Test
    void register_ShouldReturnJwtResponse_Success() throws Exception {
        when(authService.register(any()))
                .thenReturn(new JwtResponse("Registered successfully. Check your e-mail for verification."));
        
        User user = new User();
        user.setEmail("demo@gmail.com");
        user.setPassword("Demo@123");
        user.setRole(UserRole.STUDENT);
        
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Registered successfully. Check your e-mail for verification."));
    }
    
    @Test
    void login_ShouldReturnLoginResponse_Success() throws Exception {
        String email = "demo@gmail.com";
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("Demo@123");
        
        LoginResponse expectedResponse = new LoginResponse();
        expectedResponse.setAccessToken("access-token-123");
        expectedResponse.setRefreshToken("refresh-token-456");
        expectedResponse.setEmail(email);
        expectedResponse.setRole("STUDENT");
        
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }
    
    /**
     * Tests login API when no email is provided.
     *
     * Expectation:
     * - Controller should return HTTP 401 Bad Request.
     *
     * @throws IllegalArgumentException if the mock request fails
     */
    @Test
    void login_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("Demo@123");
        
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void delete_ShouldReturnJwtResponse_Success() throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.setAccessToken("access-token-123");
        
        JwtResponse expectedResponse = new JwtResponse("Account deleted successfully");
        
        when(authService.delete(any(DeleteRequest.class))).thenReturn(expectedResponse);
        
        mockMvc.perform(delete("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));
    }
//
//    @Test
//    void delete_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
//        // Arrange - Invalid delete request (null email)
//        DeleteRequest deleteRequest = new DeleteRequest();
//        deleteRequest.setPassword("Demo@123");
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/delete")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(deleteRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void refreshToken_ShouldReturnJwtResponse_Success() throws Exception {
//        // Arrange
//        RefreshRequest refreshRequest = new RefreshRequest();
//        refreshRequest.setRefreshToken("valid-refresh-token-123");
//
//        JwtResponse expectedResponse = new JwtResponse("Token refreshed successfully");
//
//        when(authService.refreshToken(anyString()))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
//    }
//
//    @Test
//    void refreshToken_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
//        // Arrange - Invalid refresh request (null token)
//        RefreshRequest refreshRequest = new RefreshRequest();
//
//        // Act & Assert
//        mockMvc.perform(post("/api/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnJwtResponse_Success() throws Exception {
//        // Arrange
//        String verificationToken = "valid-verification-token-123";
//        JwtResponse expectedResponse = new JwtResponse("Email verified successfully");
//
//        when(authService.verifyEmail(anyString()))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/verify-email")
//                        .param("token", verificationToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Email verified successfully"));
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnBadRequest_WhenTokenMissing() throws Exception {
//        // Act & Assert - Missing token parameter
//        mockMvc.perform(get("/api/verify-email"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnBadRequest_WhenTokenEmpty() throws Exception {
//        // Act & Assert - Empty token parameter
//        mockMvc.perform(get("/api/verify-email")
//                        .param("token", ""))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Additional edge case tests
//
//    @Test
//    void register_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
//        // Arrange
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("invalid-email");
//        registerRequest.setPassword("Demo@123");
//        registerRequest.setRole(UserRole.STUDENT);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void register_ShouldReturnBadRequest_WhenPasswordTooWeak() throws Exception {
//        // Arrange
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("demo@gmail.com");
//        registerRequest.setPassword("123"); // Weak password
//        registerRequest.setRole(UserRole.STUDENT);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
//        // Arrange
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setEmail("demo@gmail.com");
//        loginRequest.setPassword("WrongPassword");
//
//        when(authService.login(any(LoginRequest.class)))
//                .thenThrow(new RuntimeException("Invalid credentials"));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isInternalServerError()); // Assuming your GlobalExceptionHandler maps RuntimeException to 500
//    }
//
//    @Test
//    void refreshToken_ShouldReturnUnauthorized_WhenInvalidRefreshToken() throws Exception {
//        // Arrange
//        RefreshRequest refreshRequest = new RefreshRequest();
//        refreshRequest.setRefreshToken("invalid-refresh-token");
//
//        when(authService.refreshToken(anyString()))
//                .thenThrow(new RuntimeException("Invalid refresh token"));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isInternalServerError()); // Assuming your GlobalExceptionHandler maps RuntimeException to 500
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnBadRequest_WhenInvalidToken() throws Exception {
//        // Arrange
//        String invalidToken = "invalid-verification-token";
//
//        when(authService.verifyEmail(anyString()))
//                .thenThrow(new RuntimeException("Invalid verification token"));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/verify-email")
//                        .param("token", invalidToken))
//                .andExpect(status().isInternalServerError()); // Assuming your GlobalExceptionHandler maps RuntimeException to 500
//    }
//
//    @Test
//    void delete_ShouldReturnSuccess_WhenValidCredentials() throws Exception {
//        // Arrange
//        DeleteRequest deleteRequest = new DeleteRequest();
//        deleteRequest.setEmail("demo@gmail.com");
//        deleteRequest.setPassword("Demo@123");
//
//        JwtResponse expectedResponse = new JwtResponse("Account deleted successfully");
//
//        when(authService.delete(any(DeleteRequest.class)))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/delete")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(deleteRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Account deleted successfully"));
//    }
//
//    // Test for malformed JSON
//    @Test
//    void register_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{invalid-json"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void login_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{invalid-json"))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Test for missing Content-Type
//    @Test
//    void register_ShouldReturnUnsupportedMediaType_WhenMissingContentType() throws Exception {
//        // Arrange
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("demo@gmail.com");
//        registerRequest.setPassword("Demo@123");
//        registerRequest.setRole(UserRole.STUDENT);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/register")
//                        .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isUnsupportedMediaType());
//    }
//
//    // Test for null request body
//    @Test
//    void login_ShouldReturnBadRequest_WhenNullRequestBody() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Test for empty request body
//    @Test
//    void delete_ShouldReturnBadRequest_WhenEmptyRequestBody() throws Exception {
//        // Act & Assert
//        mockMvc.perform(delete("/api/delete")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Additional success scenarios with different data
//
//    @Test
//    void register_ShouldReturnJwtResponse_WhenTeacherRole() throws Exception {
//        // Arrange
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("teacher@gmail.com");
//        registerRequest.setPassword("Teacher@123");
//        registerRequest.setRole(UserRole.TEACHER);
//
//        when(authService.register(any(RegisterRequest.class)))
//                .thenReturn(new JwtResponse("Registered successfully. Check your e-mail for verification."));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registerRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message")
//                        .value("Registered successfully. Check your e-mail for verification."));
//    }
//
//    @Test
//    void login_ShouldReturnLoginResponse_WhenValidTeacherCredentials() throws Exception {
//        // Arrange
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setEmail("teacher@gmail.com");
//        loginRequest.setPassword("Teacher@123");
//
//        LoginResponse expectedResponse = new LoginResponse();
//        expectedResponse.setAccessToken("teacher-access-token");
//        expectedResponse.setRefreshToken("teacher-refresh-token");
//        expectedResponse.setMessage("Teacher login successful");
//
//        when(authService.login(any(LoginRequest.class)))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("teacher-access-token"))
//                .andExpect(jsonPath("$.refreshToken").value("teacher-refresh-token"))
//                .andExpect(jsonPath("$.message").value("Teacher login successful"));
//    }
//
//    @Test
//    void refreshToken_ShouldReturnNewJwtResponse_WhenValidRefreshToken() throws Exception {
//        // Arrange
//        RefreshRequest refreshRequest = new RefreshRequest();
//        refreshRequest.setRefreshToken("valid-refresh-token-789");
//
//        JwtResponse expectedResponse = new JwtResponse("Token refreshed successfully");
//
//        when(authService.refreshToken("valid-refresh-token-789"))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(refreshRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnJwtResponse_WhenValidVerificationToken() throws Exception {
//        // Arrange
//        String verificationToken = "valid-verification-token-abc123";
//        JwtResponse expectedResponse = new JwtResponse("Email verified successfully. You can now login.");
//
//        when(authService.verifyEmail(verificationToken))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/verify-email")
//                        .param("token", verificationToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Email verified successfully. You can now login."));
//    }
//
//    @Test
//    void delete_ShouldReturnJwtResponse_WhenValidTeacherCredentials() throws Exception {
//        // Arrange
//        DeleteRequest deleteRequest = new DeleteRequest();
//        deleteRequest.setEmail("teacher@gmail.com");
//        deleteRequest.setPassword("Teacher@123");
//
//        JwtResponse expectedResponse = new JwtResponse("Teacher account deleted successfully");
//
//        when(authService.delete(any(DeleteRequest.class)))
//                .thenReturn(expectedResponse);
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/delete")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(deleteRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Teacher account deleted successfully"));
//    }
//
//    // Test HTTP method not allowed scenarios
//    @Test
//    void register_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
//        // Act & Assert
//        mockMvc.perform(get("/api/register"))
//                .andExpect(status().isMethodNotAllowed());
//    }
//
//    @Test
//    void login_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
//        // Act & Assert
//        mockMvc.perform(get("/api/login"))
//                .andExpect(status().isMethodNotAllowed());
//    }
//
//    @Test
//    void delete_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/delete"))
//                .andExpect(status().isMethodNotAllowed());
//    }
//
//    @Test
//    void refreshToken_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
//        // Act & Assert
//        mockMvc.perform(get("/api/refresh"))
//                .andExpect(status().isMethodNotAllowed());
//    }
//
//    @Test
//    void verifyEmail_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/verify-email"))
//                .andExpect(status().isMethodNotAllowed());
//    }
}