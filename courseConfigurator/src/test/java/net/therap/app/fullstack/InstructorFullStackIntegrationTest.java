package net.therap.app.fullstack;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.InstructorDTO;
import net.therap.app.dto.InstructorDtoCatalog;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.InstructorMapper;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.repository.InstructorRepository;
import net.therap.app.service.AuthorizationService;
import net.therap.app.service.InstructorService;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author gazizafor
 * @since 31/8/25
 */

@SpringBootTest
@AutoConfigureMockMvc(
    addFilters = false
)
@Transactional
@ActiveProfiles("test")
class InstructorFullStackIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private InstructorRepository instructorRepository;
    
    @Autowired
    private InstructorService instructorService;
    
    @MockitoBean
    private AuthorizationService authorizationService;
    
    @MockitoBean
    private HazelcastCacheService hazelcastCacheService;
    
    private ObjectMapper objectMapper;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private InstructorMapper instructorMapper;
    
    @Autowired
    private DtoHelper dtoHelper;
    
    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
    }
    
    @Test
    void testCreateInstructorAndGetProfile() throws Exception {
        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setName("Full Stack Tester");
        instructorDTO.setEmail("fullstack@example.com");
        instructorDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.ADMIN), any(Instructor.class), any());
        
        String reqBody = objectMapper.writeValueAsString(instructorDTO);
        
        mockMvc.perform(post("/instructors").contentType(MediaType.APPLICATION_JSON).content(reqBody)).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Full Stack Tester")).andExpect(jsonPath("$.email").value("fullstack@example.com"));
        
        Optional<Instructor> savedInstructorOptional = instructorRepository.findByEmail("fullstack@example.com");
        assertThat(savedInstructorOptional).isPresent();
        Instructor savedInstructor = savedInstructorOptional.get();
        
        when(authorizationService.getInstructorIdFromRequest(any())).thenReturn(savedInstructor.getId());
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
        
        mockMvc.perform(get("/instructors/myProfile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedInstructor.getId()))
                .andExpect(jsonPath("$.name").value("Full Stack Tester"));
    }
    
    @Test
    void getInstructorById_shouldReturnInstructorDTO() throws Exception {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("John Doe");
        instructor.setEmail("john@example.com");
        instructor.setDateOfBirth(LocalDate.of(1985, 5, 15));
        Instructor savedInstructor = instructorService.createInstructor(instructor);
        
        InstructorDTO dto = new InstructorDTO();
        dto.setId(savedInstructor.getId());
        dto.setName(savedInstructor.getName());
        
        when(hazelcastCacheService.get(any(), anyLong())).thenReturn(null); // No cache hit
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
        
        // Act & Assert
        mockMvc.perform(get("/instructors/{id}", savedInstructor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedInstructor.getId()))
                .andExpect(jsonPath("$.name").value("John Doe"));
        
        verify(hazelcastCacheService).get(CacheConstants.INSTRUCTORS, savedInstructor.getId());
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
    }
    
    @Test
    void getInstructorById_fromCache_shouldReturnCachedDTO() throws Exception {
        // Arrange
        long instructorId = 1L;
        InstructorDTO cachedDto = new InstructorDTO();
        cachedDto.setId(instructorId);
        cachedDto.setName("Cached Instructor");
        
        when(hazelcastCacheService.get(eq(CacheConstants.INSTRUCTORS), eq(instructorId))).thenReturn(cachedDto);
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(), any());
        
        // Act & Assert
        mockMvc.perform(get("/instructors/{id}", instructorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(instructorId))
                .andExpect(jsonPath("$.name").value("Cached Instructor"));
        
        verify(hazelcastCacheService).get(CacheConstants.INSTRUCTORS, instructorId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(), any());
    }
    
    @Test
    void getAllInstructors_shouldReturnListOfInstructorDTOs() throws Exception {
        // Arrange
        Instructor instructor1 = new Instructor();
        instructor1.setName("Alice");
        instructor1.setEmail("alice@example.com");
        instructor1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        
        Instructor instructor2 = new Instructor();
        instructor2.setName("Bob");
        instructor2.setEmail("bob@example.com");
        instructor2.setDateOfBirth(LocalDate.of(1995, 2, 2));
        
        instructorService.createInstructor(instructor1);
        instructorService.createInstructor(instructor2);
        
        // We need to mock the DTOs that the DtoHelper would return
        InstructorDTO dto1 = new InstructorDTO();
        dto1.setId(instructor1.getId());
        dto1.setName(instructor1.getName());
        
        InstructorDTO dto2 = new InstructorDTO();
        dto2.setId(instructor2.getId());
        dto2.setName(instructor2.getName());
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.STUDENT), isNull(), any());
        
        // Act & Assert
        mockMvc.perform(get("/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.STUDENT), isNull(), any());
    }
    
    @Test
    void getInstructorByEmail_found_shouldReturnInstructorDtoCatalog() throws Exception {
        // Arrange
        String email = "test@example.com";
        Instructor instructor = new Instructor();
        instructor.setEmail(email);
        instructor.setName("Test Tester");
        instructor.setDateOfBirth(LocalDate.of(1990, 1, 1));
        instructorService.createInstructor(instructor);
        
        InstructorDtoCatalog dto = new InstructorDtoCatalog();
        dto.setEmail(email);
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any(HttpServletRequest.class));
        
        // Act & Assert
        mockMvc.perform(get("/instructors/byEmail/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
    }
    
    @Test
    void getInstructorByEmail_notFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        String email = "notfound@example.com";
        
        // Act & Assert
        mockMvc.perform(get("/instructors/byEmail/{email}", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Instructor not found"));
    }
    
//    @Test
//    void updateInstructor_existingInstructor_shouldReturnOk() throws Exception {
//        // Arrange
//        Instructor instructor = new Instructor();
//        instructor.setName("Old Name");
//        instructor.setEmail("old@example.com");
//        instructor.setDateOfBirth(LocalDate.of(1980, 1, 1));
//        Instructor savedInstructor = instructorService.createInstructor(instructor);
//
//        InstructorDTO instructorDTO = new InstructorDTO();
//        instructorDTO.setId(savedInstructor.getId());
//        instructorDTO.setName("Updated Name");
//        instructorDTO.setEmail("updated@example.com");
//
//        Instructor updatedInstructor = new Instructor();
//        updatedInstructor.setId(savedInstructor.getId());
//        updatedInstructor.setName(instructorDTO.getName());
//        updatedInstructor.setEmail(instructorDTO.getEmail());
//
//        when(instructorService.getInstructorById(savedInstructor.getId())).thenReturn(Optional.of(savedInstructor));
//        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
//        when(instructorMapper.updateInstructorFromDto(any(InstructorDTO.class), any(Instructor.class))).thenReturn(updatedInstructor);
//        when(instructorService.updateInstructor(any(Instructor.class))).thenReturn(updatedInstructor);
//        when(dtoHelper.toInstructorDTO(any(Instructor.class))).thenReturn(instructorDTO);
//
//        String reqBody = objectMapper.writeValueAsString(instructorDTO);
//
//        // Act & Assert
//        mockMvc.perform(patch("/instructors/{id}", savedInstructor.getId())
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(reqBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(savedInstructor.getId()))
//                .andExpect(jsonPath("$.name").value("Updated Name"));
//
//        verify(instructorService).getInstructorById(savedInstructor.getId());
//        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
//        verify(instructorMapper).updateInstructorFromDto(any(InstructorDTO.class), any(Instructor.class));
//        verify(instructorService).updateInstructor(any(Instructor.class));
//        verify(dtoHelper).toInstructorDTO(any(Instructor.class));
//    }
    
    @Test
    void softDeleteInstructor_existingInstructor_shouldReturnNoContent() throws Exception {
        // Arrange
        Instructor instructor = new Instructor();
        instructor.setName("To Be Deleted");
        instructor.setEmail("delete@example.com");
        instructor.setDateOfBirth(LocalDate.of(1980, 1, 1));
        Instructor savedInstructor = instructorService.createInstructor(instructor);
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
        
        // Act & Assert
        mockMvc.perform(delete("/instructors/{id}", savedInstructor.getId()))
                .andExpect(status().isNoContent());
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), any(Instructor.class), any());
    }
}
