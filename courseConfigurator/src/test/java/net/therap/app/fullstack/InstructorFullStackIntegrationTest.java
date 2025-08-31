package net.therap.app.fullstack;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.therap.app.dto.InstructorDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        
        mockMvc.perform(get("/instructors/myProfile").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(savedInstructor.getId())).andExpect(jsonPath("$.name").value("Full Stack Tester"));
    }
}
