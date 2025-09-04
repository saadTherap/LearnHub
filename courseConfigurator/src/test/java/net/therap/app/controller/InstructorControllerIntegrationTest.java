package net.therap.app.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.InstructorDTO;
import net.therap.app.dto.InstructorDtoCatalog;
import net.therap.app.exception.GlobalExceptionHandler;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.InstructorMapper;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.AuthorizationService;
import net.therap.app.service.InstructorService;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author gazizafor
 * @since 28/8/25
 */
@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {InstructorController.class, GlobalExceptionHandler.class})
class InstructorControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    Logger log = LoggerFactory.getLogger(InstructorControllerIntegrationTest.class);
    @MockitoBean
    private InstructorService instructorService;
    @MockitoBean
    private DtoHelper dtoHelper;
    @MockitoBean
    private InstructorMapper instructorMapper;
    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;
    @MockitoBean
    private AuthorizationService authorizationService;
    @MockitoBean
    private MessageSource messageSource;
    @MockitoBean
    private HttpServletRequest httpServletRequest; // Mock HttpServletRequest
    
    @Mock
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
    }
    
    @Test
    void getMyProfile_shouldReturnInstructorDTO() throws Exception {
        // Arrange
        long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        InstructorDTO dto = new InstructorDTO();
        dto.setId(instructorId);
        
        when(authorizationService.getInstructorIdFromRequest(any(HttpServletRequest.class))).thenReturn(instructorId);
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.of(instructor));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                                         any(HttpServletRequest.class));
        when(instructorMapper.toInstructorDTO(any(Instructor.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/instructors/myProfile")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(instructorId));
        
        verify(authorizationService).getInstructorIdFromRequest(any(HttpServletRequest.class));
        verify(instructorService).getInstructorById(instructorId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                               any(HttpServletRequest.class));
        verify(instructorMapper).toInstructorDTO(any(Instructor.class));
    }
    
    @Test
    void getInstructorById_fromCache_shouldReturnCachedDTO() throws Exception {
        // Arrange
        long instructorId = 1L;
        InstructorDTO cachedDto = new InstructorDTO();
        cachedDto.setId(instructorId);
        
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTORS), eq(instructorId))).thenReturn(cachedDto);
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(cachedDto),
                                                         any(HttpServletRequest.class));
        
        // Act & Assert
        mockMvc.perform(get("/instructors/{id}", instructorId)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(instructorId));
        
        verify(hazelcastCacheService).get(CacheConstants.INSTRUCTORS, instructorId);
        verify(instructorService, never()).getInstructorById(anyLong());
    }
    
    @Test
    void getAllInstructors_shouldReturnListOfInstructorDTOs() throws Exception {
        // Arrange
        Instructor instructor = new Instructor();
        InstructorDTO dto = new InstructorDTO();
        List<Instructor> instructors = Collections.singletonList(instructor);
        List<InstructorDTO> dtoList = Collections.singletonList(dto);
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT), isNull(),
                                                         any(HttpServletRequest.class));
        when(instructorService.getAllInstructors()).thenReturn(instructors);
        when(dtoHelper.toInstructorDTO(any(Instructor.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/instructors")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$[0]").exists()); // Just check if at least one DTO is returned
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.STUDENT), isNull(), any(HttpServletRequest.class));
        verify(instructorService).getAllInstructors();
        verify(dtoHelper).toInstructorDTO(any(Instructor.class));
    }
    
    @Test
    void createInstructor_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setName("New Instructor");
        instructorDTO.setEmail("new@example.com");
        instructorDTO.setDateOfBirth(LocalDate.of(2010, 12, 25));
        
        Instructor instructorToCreate = new Instructor();
        instructorToCreate.setName("New Instructor");
        Instructor createdInstructor = new Instructor();
        createdInstructor.setId(1L);
        createdInstructor.setName("New Instructor");
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.ADMIN), any(Instructor.class),
                                                         any(HttpServletRequest.class));
        when(instructorMapper.toInstructor(any(InstructorDTO.class))).thenReturn(instructorToCreate);
        when(instructorService.createInstructor(any(Instructor.class))).thenReturn(createdInstructor);
        when(dtoHelper.toInstructorDTO(any(Instructor.class))).thenReturn(instructorDTO);
        
        String reqBody = objectMapper.writeValueAsString(instructorDTO);
        log.info("--------------------------------------");
        log.info("{}", reqBody);
        log.info("--------------------------------------");
        log.info("{}", instructorDTO);
        // Act & Assert
        mockMvc.perform(post("/instructors").contentType(MediaType.APPLICATION_JSON).content(reqBody)).andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.name").value("New Instructor"));
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.ADMIN), any(Instructor.class),
                                               any(HttpServletRequest.class));
        verify(instructorMapper).toInstructor(any(InstructorDTO.class));
        verify(instructorService).createInstructor(any(Instructor.class));
        verify(dtoHelper).toInstructorDTO(any(Instructor.class));
    }
    
    @Test
    void updateInstructor_existingInstructor_shouldReturnOk() throws Exception {
        // Arrange
        long instructorId = 1L;
        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setId(instructorId);
        instructorDTO.setName("Updated Name");
        instructorDTO.setEmail("updated@example.com");
        
        Instructor existingInstructor = new Instructor();
        existingInstructor.setId(instructorId);
        existingInstructor.setName("Old Name");
        existingInstructor.setEmail("old@example.com");
        
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.of(existingInstructor));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(existingInstructor),
                                                         any(HttpServletRequest.class));
        doNothing().when(instructorMapper).updateInstructorFromDto(any(InstructorDTO.class), any(Instructor.class));
        when(instructorService.updateInstructor(any(Instructor.class))).thenReturn(existingInstructor); // Return the
        // updated existingInstructor
        when(dtoHelper.toInstructorDTO(any(Instructor.class))).thenReturn(instructorDTO); // Return the DTO
        // reflecting updates
        
        // Act & Assert
        mockMvc.perform(patch("/instructors/{id}", instructorId).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(instructorDTO))).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(instructorId)).andExpect(jsonPath("$.name").value("Updated Name")); // Assert updated name
        
        verify(instructorService).getInstructorById(instructorId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(existingInstructor),
                                               any(HttpServletRequest.class));
        verify(instructorMapper).updateInstructorFromDto(any(InstructorDTO.class), eq(existingInstructor));
        verify(instructorService).updateInstructor(eq(existingInstructor));
        verify(dtoHelper).toInstructorDTO(eq(existingInstructor));
    }
    
    @Test
    void softDeleteInstructor_existingInstructor_shouldReturnNoContent() throws Exception {
        // Arrange
        long instructorId = 1L;
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        instructor.setDeleted(true); // Assuming the service sets this
        
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.of(instructor));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                                         any(HttpServletRequest.class));
        // Mock the return value of deleteById
        when(instructorService.deleteById(instructorId)).thenReturn(instructor);
        
        // Act & Assert
        mockMvc.perform(delete("/instructors/{id}", instructorId)).andExpect(status().isNoContent());
        
        verify(instructorService).getInstructorById(instructorId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                               any(HttpServletRequest.class));
        verify(instructorService).deleteById(instructorId);
    }
    
    @Test
    void getInstructorById_notFound_shouldReturnNotFound() throws Exception {
        // Arrange
        long instructorId = 1L;
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTORS), eq(instructorId))).thenReturn(null);
        when(instructorService.getInstructorById(instructorId)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/instructors/{id}", instructorId)).andExpect(status().isNotFound());
    }
    
    @Test
    void getInstructorByEmail_found_shouldReturnInstructorDtoCatalog() throws Exception {
        // Arrange
        String email = "test@example.com";
        Instructor instructor = new Instructor();
        instructor.setEmail(email);
        InstructorDtoCatalog dto = new InstructorDtoCatalog();
        dto.setEmail(email);
        
        when(instructorService.getByEmailNonDeleted(email)).thenReturn(Optional.of(instructor));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                                         any(HttpServletRequest.class));
        when(instructorMapper.toInstructorDtoCatalog(any(Instructor.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/instructors/byEmail/{email}", email)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.email").value(email));
        
        verify(instructorService).getByEmailNonDeleted(email);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(instructor),
                                               any(HttpServletRequest.class));
        verify(instructorMapper).toInstructorDtoCatalog(any(Instructor.class));
    }
    
    @Test
    void getInstructorByEmail_notFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        String email = "notfound@example.com";
        when(instructorService.getByEmailNonDeleted(email)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("not.found.instructor"), any(), any(Locale.class))).thenReturn("Instructor not found");
        
        // Act & Assert
        mockMvc.perform(get("/instructors/byEmail/{email}", email)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Instructor not found"));
        
        verify(instructorService).getByEmailNonDeleted(email);
        verify(messageSource).getMessage(eq("not.found.instructor"), any(), any(Locale.class));
    }
}