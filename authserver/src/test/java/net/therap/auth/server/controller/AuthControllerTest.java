//package net.therap.auth.server.controller;
//
//import net.therap.auth.server.dto.JwtResponse;
//import net.therap.auth.server.exception.AuthServerException;
//import net.therap.auth.server.handler.GlobalExceptionHandler;
//import net.therap.auth.server.service.interfaces.AuthService;
//import net.therap.auth.server.util.MessageUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.MessageSource;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author apurboturjo
// * @since 8/27/25
// */
//class AuthControllerTest {
//
//    private MockMvc mockMvc;
//    private AuthService authService;
//
//    @BeforeEach
//    void setup() {
//        authService = mock(AuthService.class);
//
//        AuthController authController = new AuthController(authService);
//
//        MessageSource messageSource = mock(MessageSource.class);
//        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Email already exists");
//
//        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
//
//        mockMvc = MockMvcBuilders.standaloneSetup(authController)
//                .setControllerAdvice(exceptionHandler)
//                .build();
//    }
//
//    @Test
//    void register_ShouldReturnJwtResponse_Success() throws Exception {
//        when(authService.register(any()))
//                .thenReturn(new JwtResponse("Registered successfully. Check your e-mail for verification."));
//
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {"email": "demo@gmail.com", "password": "Demo@123", "role": "STUDENT"}
//                                """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Registered successfully. Check your e-mail for verification."));
//    }
//
//    @Test
//    void register_ShouldReturnUnauthorized_WhenEmailExists() throws Exception {
//        doThrow(new AuthServerException("Email already exists"))
//                .when(authService).register(any());
//
//        mockMvc.perform(post("/api/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {"email": "demo@gmail.com", "password": "Demo@123", "role": "STUDENT"}
//                                """))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("Email already exists"));
//    }
//}