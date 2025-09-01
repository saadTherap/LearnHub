package net.therap.auth.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.therap.auth.server.entity.User;
import net.therap.auth.server.enums.UserRole;
import net.therap.auth.server.exception.AuthServerException;
import net.therap.auth.server.handler.GlobalExceptionHandler;
import net.therap.auth.server.service.UserService;
import net.therap.auth.server.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author apurboturjo
 * @since 8/31/25
 */
@WebMvcTest
@ContextConfiguration(classes = {AdminController.class})
@Import({GlobalExceptionHandler.class, MessageUtil.class})
class AdminControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private UserService userService;
    
    @Autowired
    private MessageUtil messageUtil;
    
    @Test
    void getMe_ShouldReturnUser_Success() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@gmail.com");
        adminUser.setRole(UserRole.ADMIN);
        
        when(userService.findById(1L)).thenReturn(adminUser);
        
        mockMvc.perform(get("/admin/me")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    
    @Test
    void getAllUsers_ShouldReturnUserList_Success() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@gmail.com");
        adminUser.setRole(UserRole.ADMIN);

        User studentUser = new User();
        studentUser.setId(2L);
        studentUser.setEmail("student@gmail.com");
        studentUser.setRole(UserRole.STUDENT);

        when(userService.findById(1L)).thenReturn(adminUser);
        when(userService.findAll()).thenReturn(Arrays.asList(adminUser, studentUser));

        mockMvc.perform(get("/admin/users")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("admin@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("student@gmail.com"));
    }

    @Test
    void getUserById_ShouldReturnUser_Success() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ADMIN);

        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setEmail("student@gmail.com");
        targetUser.setRole(UserRole.STUDENT);

        when(userService.findById(1L)).thenReturn(adminUser);
        when(userService.findById(2L)).thenReturn(targetUser);

        mockMvc.perform(get("/admin/user/2")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.email").value("student@gmail.com"));
    }

    @Test
    void deleteUser_ShouldReturnOk_Success() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ADMIN);

        when(userService.findById(1L)).thenReturn(adminUser);
        doNothing().when(userService).deleteById(2L);

        mockMvc.perform(delete("/admin/delete-user/2")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk());
    }
    
    @Test
    void forceLogout_ShouldReturnOk_Success() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ADMIN);
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", 2L);
        
        when(userService.findById(1L)).thenReturn(adminUser);
        doNothing().when(userService).forceLogout(2L);
        
        mockMvc.perform(post("/admin/logout-force")
                        .requestAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isOk());
    }

    @Test
    void getMe_ShouldReturnUnauthorized_WhenNoUserId() throws Exception {
        mockMvc.perform(get("/admin/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(MessageUtil.getMessage("err.admin.auth")));
    }

    @Test
    void getAllUsers_ShouldReturnUnauthorized_WhenNotAdmin() throws Exception {
        User studentUser = new User();
        studentUser.setId(1L);
        studentUser.setRole(UserRole.STUDENT);

        when(userService.findById(1L)).thenReturn(studentUser);

        mockMvc.perform(get("/admin/users")
                        .requestAttr("userId", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(MessageUtil.getMessage("err.admin.access")));
    }

    @Test
    void getUserById_ShouldReturnUnauthorized_WhenInstructorUser() throws Exception {
        User instructorUser = new User();
        instructorUser.setId(1L);
        instructorUser.setRole(UserRole.INSTRUCTOR);

        when(userService.findById(1L)).thenReturn(instructorUser);

        mockMvc.perform(get("/admin/user/2")
                        .requestAttr("userId", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(MessageUtil.getMessage("err.admin.access")));
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ADMIN);

        when(userService.findById(1L)).thenReturn(adminUser);
        doThrow(new AuthServerException("User not found")).when(userService).deleteById(999L);
        
        mockMvc.perform(delete("/admin/delete-user/999")
                        .requestAttr("userId", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void forceLogout_ShouldReturnBadRequest_WhenMissingUserId() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ADMIN);

        Map<String, Object> requestData = new HashMap<>();

        when(userService.findById(1L)).thenReturn(adminUser);

        mockMvc.perform(post("/admin/logout-force")
                        .requestAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMe_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
        mockMvc.perform(post("/admin/me"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void deleteUser_ShouldReturnMethodNotAllowed_WhenUsingPostMethod() throws Exception {
        mockMvc.perform(post("/admin/delete-user/1"))
                .andExpect(status().isMethodNotAllowed());
    }
}