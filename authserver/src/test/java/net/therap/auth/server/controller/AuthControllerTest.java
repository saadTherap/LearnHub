package net.therap.auth.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.auth.server.dto.*;
import net.therap.auth.server.entity.AuthKey;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.KeyStatus;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.handler.GlobalExceptionHandler;
import net.therap.auth.server.service.AuthKeyService;
import net.therap.auth.server.service.JwtService;
import net.therap.auth.server.service.interfaces.AuthService;
import net.therap.auth.server.util.MessageUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Base64;

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
@Import({GlobalExceptionHandler.class, MessageUtil.class})
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private AuthService authService;
    
    @Autowired
    private MessageUtil messageUtil;
    
    @MockitoBean
    private JwtService jwtService;
    
    @MockitoBean
    private AuthKeyService authKeyService;
    
    @BeforeAll
    static void setup(@Autowired AuthKeyService authKeyService) {
        AuthKey fakeKey = AuthKey.builder()
                .kid("test-key")
                .publicKey(Base64.getEncoder().encodeToString("public".getBytes()))
                .privateKey(Base64.getEncoder().encodeToString("private".getBytes()))
                .status(KeyStatus.ACTIVE)
                .build();
        
        when(authKeyService.getActiveKey()).thenReturn(fakeKey);
    }
    
    
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
        Long userId = 1L;
        JwtResponse expectedResponse = new JwtResponse("Account deleted successfully");
        
        when(authService.delete(any(Long.class))).thenReturn(expectedResponse);
        
        mockMvc.perform(delete("/api/delete")
                        .requestAttr("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));
    }
    
    @Test
    void delete_ShouldReturnBadRequest_WhenNoAccessToken() throws Exception {
        mockMvc.perform(delete("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_Success() throws Exception {
        UpdateUserRequest userToUpdate = new UpdateUserRequest();
        userToUpdate.setPassword("Demo@123");
        
        JwtResponse response = new JwtResponse(MessageUtil.getMessage("ok.user.updated"));
        
        when(authService.updateUser(any(UpdateUserRequest.class))).thenReturn(response);
        
        mockMvc.perform(put("/api/update-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageUtil.getMessage("ok.user.updated")));
    }
    
    @Test
    void refreshToken_ShouldReturnJwtResponse_Success() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("valid-refresh-token-123");
        
        JwtResponse expectedResponse = new JwtResponse("Token refreshed successfully");
        
        when(authService.refreshToken(anyString())).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
    }
    
    @Test
    void refreshToken_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        
        mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void verifyEmail_ShouldReturnJwtResponse_Success() throws Exception {
        String verificationToken = "valid-verification-token-123";
        
        JwtResponse expectedResponse = new JwtResponse("Email verified successfully");
        
        when(authService.verifyEmail(anyString())).thenReturn(expectedResponse);
        
        mockMvc.perform(get("/api/verify-email")
                        .param("token", verificationToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));
    }
    
    @Test
    void verifyEmail_ShouldReturnBadRequest_WhenTokenMissing() throws Exception {
        mockMvc.perform(get("/api/verify-email"))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Provided token is Blank
     *
     * @throws HandlerMethodValidationException if the provided token is Blank
     */
    @Test
    void verifyEmail_ShouldReturnBadRequest_WhenTokenEmpty() throws Exception {
        mockMvc.perform(get("/api/verify-email")
                        .param("token", ""))
                .andExpect(status().isInternalServerError());
    }
    
    // --------------------- Additional edge case tests ---------------------------- //
    
    @Test
    void register_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("invalid-email");
        registerRequest.setPassword("Demo@123");
        registerRequest.setRole(UserRole.STUDENT.name());
        
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void register_ShouldReturnBadRequest_WhenPasswordTooWeak() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("demo@gmail.com");
        registerRequest.setPassword("123");
        registerRequest.setRole(UserRole.STUDENT.name());
        
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("demo@gmail.com");
        loginRequest.setPassword("WrongPassword");
        
        when(authService.login(any(LoginRequest.class))).thenThrow(new AuthServerException("Invalid credentials"));
        
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
    
    @Test
    void refreshToken_ShouldReturnUnauthorized_WhenInvalidRefreshToken() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("invalid-refresh-token");
        
        when(authService.refreshToken(anyString())).thenThrow(new AuthServerException("Invalid refresh token"));
        
        mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));
    }
    
    @Test
    void verifyEmail_ShouldReturnBadRequest_WhenInvalidToken() throws Exception {
        String invalidToken = "invalid-verification-token";
        
        when(authService.verifyEmail(anyString())).thenThrow(new AuthServerException("Invalid verification token"));
        
        mockMvc.perform(get("/api/verify-email")
                        .param("token", invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid verification token"));
    }
    
    /**
     * Test for malformed JSON or Empty request body
     */
    @Test
    void register_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Thrown when Spring cannot read or parse the HTTP request body.
     * Common causes:
     * - Malformed JSON
     * - Null or missing body when @RequestBody is expected
     *
     * @throws HttpMessageNotReadableException when the login request body is null
     */
    @Test
    void login_ShouldReturnBadRequest_WhenNullRequestBody() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    
    // ------------------- Additional success scenarios with different data -------------------- //
    
    @Test
    void register_ShouldReturnJwtResponse_WhenTeacherRole() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("instructor1@gmail.com");
        registerRequest.setPassword("Demo@123");
        registerRequest.setRole(UserRole.INSTRUCTOR.name());
        
        String msg = "Registered successfully. Check your e-mail for verification.";
        
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new JwtResponse(msg));
        
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(msg));
    }
    
    @Test
    void refreshToken_ShouldReturnNewJwtResponse_WhenValidRefreshToken() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("valid-refresh-token-123");
        
        String msg = "Token refreshed successfully";
        
        JwtResponse expectedResponse = new JwtResponse(msg);
        
        when(authService.refreshToken("valid-refresh-token-123")).thenReturn(expectedResponse);
        
        mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(msg));
    }
    
    // ----------------- Test HTTP method not allowed scenarios ----------------- //
    @Test
    void register_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
        mockMvc.perform(get("/api/register"))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    void login_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
        mockMvc.perform(get("/api/login"))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    void delete_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
        mockMvc.perform(post("/api/delete"))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    void refreshToken_ShouldReturnMethodNotAllowed_WhenUsingGetMethod() throws Exception {
        mockMvc.perform(get("/api/refresh"))
                .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    void verifyEmail_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
        mockMvc.perform(post("/api/verify-email"))
                .andExpect(status().isMethodNotAllowed());
    }
}