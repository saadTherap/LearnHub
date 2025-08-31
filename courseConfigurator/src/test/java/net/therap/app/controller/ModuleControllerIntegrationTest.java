package net.therap.app.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import net.therap.app.config.JacksonConfig;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.ContentCatalogueDTO;
import net.therap.app.dto.ModuleDTO;
import net.therap.app.dto.ReorderDTO;
import net.therap.app.service.AuthorizationService;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.ModuleMapper;
import net.therap.app.model.Content;
import net.therap.app.model.Course;
import net.therap.app.model.Module;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.ContentService;
import net.therap.app.service.CourseService;
import net.therap.app.service.ModuleService;
import net.therap.app.util.CollectionUtil; // Import CollectionUtil
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static jdk.dynalink.linker.support.Guards.isNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author gazizafor
 * @since 28/8/25
 */
@ExtendWith(MockitoExtension.class)
// @Import(JacksonConfig.class) // Not needed as ObjectMapper is initialized manually for standaloneSetup
class ModuleControllerIntegrationTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private ModuleService moduleService;
    @Mock private DtoHelper dtoHelper;
    @Mock private ModuleMapper moduleMapper;
    @Mock private CourseService courseService;
    @Mock private MessageSource messageSource;
    @Mock private HazelcastCacheService hazelcastCacheService;
    @Mock private AuthorizationService authorizationService;
    @Mock private ContentService contentService;
    @Mock private HttpServletRequest httpServletRequest;
    
    private ObjectMapper objectMapper; // Manually initialized ObjectMapper
    
    @InjectMocks
    private ModuleController moduleController;
    
    @BeforeEach
    void setUp() {
        // Explicitly initialize and configure ObjectMapper for standaloneSetup
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
        
        this.mockMvc = MockMvcBuilders.standaloneSetup(moduleController)
                .setControllerAdvice(new net.therap.app.exception.GlobalExceptionHandler(messageSource))
                .build();
    }
    
    // --- GET Methods ---
    @Test
    void getAllModules_shouldReturnListOfModuleDTOs() throws Exception {
        // Arrange
        Module module = new Module();
        ModuleDTO dto = new ModuleDTO();
        List<Module> modules = Collections.singletonList(module);
        List<ModuleDTO> dtoList = Collections.singletonList(dto);
        
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.ADMIN), isNull(), any(HttpServletRequest.class));
        when(moduleService.findAll()).thenReturn(modules);
        when(dtoHelper.toModuleDTO(any(Module.class))).thenReturn(dto);
        
        // Act & Assert
        mockMvc.perform(get("/modules"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
        
        verify(authorizationService).authorize(eq(AuthorizationLevel.ADMIN), isNull(), any(HttpServletRequest.class));
        verify(moduleService).findAll();
        verify(dtoHelper).toModuleDTO(any(Module.class));
    }
    
    @Test
    void getModulesByCourse_fromCache_shouldReturnCachedDTOs() throws Exception {
        // Arrange
        long courseId = 1L;
        ModuleDTO cachedDto = new ModuleDTO();
        cachedDto.setCourseId(courseId);
        List<ModuleDTO> cachedList = Collections.singletonList(cachedDto);
        
        when(hazelcastCacheService.get(eq(CacheConstants.MODULES_BY_COURSE), eq(courseId))).thenReturn(cachedList);
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(cachedList), any(HttpServletRequest.class));
        
        // Act & Assert
        mockMvc.perform(get("/modules/byCourse/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].courseId").value(courseId));
        
        verify(hazelcastCacheService).get(CacheConstants.MODULES_BY_COURSE, courseId);
        verify(moduleService, never()).findByCourseId(anyLong());
    }
    
    @Test
    void getModulesByCourse_cacheMiss_shouldReturnDTOsFromDb() throws Exception {
        // Arrange
        long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        Module module = new Module();
        module.setCourse(course); // Assuming Course has a constructor for ID
        ModuleDTO dto = new ModuleDTO();
        dto.setCourseId(courseId);
        List<Module> modules = Collections.singletonList(module);
        List<ModuleDTO> dtoList = Collections.singletonList(dto);
        
        when(hazelcastCacheService.get(eq(CacheConstants.MODULES_BY_COURSE), eq(courseId))).thenReturn(null);
        when(moduleService.findByCourseId(courseId)).thenReturn(modules);
        when(dtoHelper.toModuleDtoLazy(any(Module.class))).thenReturn(dto);
        doNothing().when(hazelcastCacheService).put(eq(CacheConstants.MODULES_BY_COURSE), eq(courseId), eq(dtoList)); // Cache put
        
        // Act & Assert
        mockMvc.perform(get("/modules/byCourse/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].courseId").value(courseId));
        
        verify(moduleService).findByCourseId(courseId);
        verify(dtoHelper).toModuleDtoLazy(any(Module.class));
        verify(hazelcastCacheService).put(eq(CacheConstants.MODULES_BY_COURSE), eq(courseId), eq(dtoList));
    }
    
    @Test
    void getModuleById_fromCache_shouldReturnCachedDTO() throws Exception {
        // Arrange
        long moduleId = 1L;
        ModuleDTO cachedDto = new ModuleDTO();
        cachedDto.setId(moduleId);
        
        when(hazelcastCacheService.get(eq(CacheConstants.MODULES), eq(moduleId))).thenReturn(cachedDto);
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(cachedDto), any(HttpServletRequest.class));
        
        // Act & Assert
        mockMvc.perform(get("/modules/{id}", moduleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(moduleId));
        
        verify(hazelcastCacheService).get(CacheConstants.MODULES, moduleId);
        verify(moduleService, never()).findById(anyLong());
    }
    
    @Test
    void getModuleById_cacheMiss_shouldReturnDTOFromDb() throws Exception {
        // Arrange
        long moduleId = 1L;
        Module module = new Module();
        module.setId(moduleId);
        ModuleDTO dto = new ModuleDTO();
        dto.setId(moduleId);
        
        when(hazelcastCacheService.get(eq(CacheConstants.MODULES), eq(moduleId))).thenReturn(null);
        when(moduleService.findById(moduleId)).thenReturn(Optional.of(module));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(module), any(HttpServletRequest.class));
        // BeanUtils.copyProperties is called in the controller, no need to mock it directly
        doNothing().when(hazelcastCacheService).put(eq(CacheConstants.MODULES), eq(moduleId), any(ModuleDTO.class));
        
        
        // Act & Assert
        mockMvc.perform(get("/modules/{id}", moduleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(moduleId));
        
        verify(moduleService).findById(moduleId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(module), any(HttpServletRequest.class));
        verify(hazelcastCacheService).put(eq(CacheConstants.MODULES), eq(moduleId), any(ModuleDTO.class));
    }
    
    @Test
    void getModuleById_notFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long moduleId = 1L;
        when(hazelcastCacheService.get(eq(CacheConstants.MODULES), eq(moduleId))).thenReturn(null);
        when(moduleService.findById(moduleId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Module not found");
        
        // Act & Assert
        mockMvc.perform(get("/modules/{id}", moduleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Module not found"));
        
        verify(moduleService).findById(moduleId);
    }
    
    // --- POST Methods ---
    @Test
    void createModule_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        long courseId = 1L;
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle("New Module Title");
        moduleDTO.setCourseId(courseId);
        moduleDTO.setOrderIndex(0L); // Default value
        
        Course course = new Course();
        course.setId(courseId);
        Module moduleToCreate = new Module();
        moduleToCreate.setTitle("New Module Title");
        moduleToCreate.setCourse(course);
        moduleToCreate.setOrderIndex(1L); // After getMaxOrderIndexOfModules + 1
        
        Module savedModule = new Module();
        savedModule.setId(10L);
        savedModule.setTitle("New Module Title");
        savedModule.setCourse(course);
        savedModule.setOrderIndex(1L);
        
        when(courseService.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(course), any(HttpServletRequest.class));
        when(moduleMapper.toModule(any(ModuleDTO.class))).thenReturn(moduleToCreate);
        when(moduleService.getMaxOrderIndexOfModules(courseId)).thenReturn(0L); // Max order is 0, so new is 1
        when(moduleService.save(any(Module.class))).thenReturn(savedModule);
        when(moduleMapper.toModuleDTO(any(Module.class))).thenReturn(moduleDTO); // Mock mapper for response
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(post("/modules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Module Title"))
                .andExpect(jsonPath("$.courseId").value(courseId));
        
        verify(courseService).findById(courseId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(course), any(HttpServletRequest.class));
        verify(moduleMapper).toModule(any(ModuleDTO.class));
        verify(moduleService).getMaxOrderIndexOfModules(courseId);
        verify(moduleService).save(any(Module.class));
        verify(moduleMapper).toModuleDTO(any(Module.class));
    }
    
    @Test
    void createModule_withInvalidTitle_shouldReturnBadRequest() throws Exception {
        // Arrange
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle(""); // Invalid title
        moduleDTO.setCourseId(1L);
        
        // Mock message source for validation error message
        when(messageSource.getMessage(eq("validation.title.notblank"), any(), any(Locale.class))).thenReturn("Title cannot be blank.");
        when(messageSource.getMessage(eq("error.validation.failed"), any(), any(Locale.class))).thenReturn("Validation failed.");
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(post("/modules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.title").value("Title cannot be blank."));
        
        verify(courseService, never()).findById(anyLong()); // Should not reach service layer
    }
    
    @Test
    void createModule_withInvalidCourseId_shouldReturnBadRequest() throws Exception {
        // Arrange
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle("Valid Title");
        moduleDTO.setCourseId(0L); // Invalid courseId (<1)
        
        // Mock message source for validation error message
        when(messageSource.getMessage(eq("validation.course.id.null"), any(), any(Locale.class))).thenReturn("Course ID cannot be zero or less.");
        when(messageSource.getMessage(eq("error.validation.failed"), any(), any(Locale.class))).thenReturn("Validation failed.");
        
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(post("/modules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.courseId").value("Course ID cannot be zero or less."));
        
        verify(courseService, never()).findById(anyLong()); // Should not reach service layer
    }
    
    @Test
    void createModule_courseNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        long courseId = 1L;
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle("New Module Title");
        moduleDTO.setCourseId(courseId);
        
        when(courseService.findById(courseId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Course not found");
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(post("/modules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
        
        verify(courseService).findById(courseId);
        verify(moduleService, never()).getMaxOrderIndexOfModules(anyLong());
    }
    
    @Test
    void reorderContents_withValidData_shouldReturnOk() throws Exception {
        // Arrange
        long moduleId = 1L;
        long contentId1 = 10L;
        long contentId2 = 11L;
        
        ReorderDTO reorder1 = new ReorderDTO();
        reorder1.setId(contentId1);
        reorder1.setOrderIndex(2L);
        
        ReorderDTO reorder2 = new ReorderDTO();
        reorder2.setId(contentId2);
        reorder2.setOrderIndex(1L);
        
        List<ReorderDTO> reorderList = Arrays.asList(reorder1, reorder2);
        
        Content content1 = new Content();
        content1.setId(contentId1);
        Content content2 = new Content();
        content2.setId(contentId2);
        List<Content> updatedContents = Arrays.asList(content1, content2);
        
        ContentCatalogueDTO dto1 = new ContentCatalogueDTO();
        dto1.setId(contentId1);
        ContentCatalogueDTO dto2 = new ContentCatalogueDTO();
        dto2.setId(contentId2);
        List<ContentCatalogueDTO> dtoList = Arrays.asList(dto1, dto2);
        
        // Mock static method CollectionUtil.isValidOrderedList
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.isValidOrderedList(anyList())).thenReturn(true);
            
            when(contentService.findById(contentId1)).thenReturn(Optional.of(content1));
            doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(content1), any(HttpServletRequest.class));
            when(moduleService.reorderContents(anyList())).thenReturn(updatedContents);
            when(dtoHelper.toContentCatalogueDTO(any(Content.class))).thenReturn(dto1, dto2);
            
            String reqBody = objectMapper.writeValueAsString(reorderList);
            
            // Act & Assert
            mockMvc.perform(post("/modules/contents/reorder")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(reqBody))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(contentId1))
                    .andExpect(jsonPath("$[1].id").value(contentId2)); // Order might change based on reorderContents logic
            
            verify(contentService).findById(contentId1);
            verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(content1), any(HttpServletRequest.class));
            verify(moduleService).reorderContents(anyList());
            verify(dtoHelper, times(2)).toContentCatalogueDTO(any(Content.class));
        }
    }
    
    @Test
    void reorderContents_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Arrange
        ReorderDTO reorder1 = new ReorderDTO();
        reorder1.setId(10L);
        reorder1.setOrderIndex(1L);
        
        ReorderDTO reorder2 = new ReorderDTO();
        reorder2.setId(10L); // Duplicate ID
        reorder2.setOrderIndex(2L);
        
        List<ReorderDTO> reorderList = Arrays.asList(reorder1, reorder2);
        
        when(messageSource.getMessage(eq("invalid.reorder"), any(), any(Locale.class)))
                .thenReturn("Invalid reorder list.");
        
        try (MockedStatic<CollectionUtil> mockedCollectionUtil = mockStatic(CollectionUtil.class)) {
            mockedCollectionUtil.when(() -> CollectionUtil.isValidOrderedList(anyList())).thenReturn(false);
            
            String reqBody = objectMapper.writeValueAsString(reorderList);
            
            mockMvc.perform(post("/modules/contents/reorder")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(reqBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid reorder list."));
            
            verify(contentService, never()).findById(anyLong()); // Should not reach service layer
        }
    }
    
    
    // --- PATCH Methods ---
    @Test
    void updateModule_existingModule_shouldReturnOk() throws Exception {
        // Arrange
        long moduleId = 1L;
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle("Updated Module Title");
        moduleDTO.setCourseId(100L); // Course ID might be updated in DTO, but not directly in controller
        
        Module existingModule = new Module();
        existingModule.setId(moduleId);
        existingModule.setTitle("Original Title");
        Course existingCourse = new Course();
        existingCourse.setId(100L);
        existingModule.setCourse(existingCourse); // Mock existing course
        
        Module updatedModule = new Module(); // This represents the module *after* being saved by the service
        updatedModule.setId(moduleId);
        updatedModule.setTitle("Updated Module Title"); // This is the title expected after update
        updatedModule.setCourse(existingCourse);
        
        // Create a DTO that reflects the state of the updatedModule
        ModuleDTO responseDTO = new ModuleDTO();
        responseDTO.setId(updatedModule.getId());
        responseDTO.setTitle(updatedModule.getTitle());
        responseDTO.setCourseId(updatedModule.getCourse().getId());
        responseDTO.setOrderIndex(updatedModule.getOrderIndex()); // Include other fields if needed for assertions
        
        
        when(moduleService.findById(moduleId)).thenReturn(Optional.of(existingModule));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(existingModule), any(HttpServletRequest.class));
        when(moduleService.save(any(Module.class))).thenReturn(updatedModule);
        when(moduleMapper.toModuleDTO(any(Module.class))).thenReturn(responseDTO);
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(patch("/modules/{id}", moduleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(moduleId))
                .andExpect(jsonPath("$.title").value("Updated Module Title")); // Assert updated title
        
        verify(moduleService).findById(moduleId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(existingModule), any(HttpServletRequest.class));
        verify(moduleService).save(any(Module.class));
        verify(moduleMapper).toModuleDTO(any(Module.class));
    }
    
    @Test
    void updateModule_withInvalidTitle_shouldReturnBadRequest() throws Exception {
        // Arrange
        long moduleId = 1L;
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle(""); // Invalid title (blank)
        moduleDTO.setCourseId(1L);
        
        Module existingModule = new Module();
        existingModule.setId(moduleId);
        existingModule.setTitle("Original Title");
        
        when(moduleService.findById(moduleId)).thenReturn(Optional.of(existingModule));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(existingModule), any(HttpServletRequest.class));
        when(messageSource.getMessage(eq("validation.title.notblank"), any(), any(Locale.class))).thenReturn("Title cannot be blank.");
        when(messageSource.getMessage(eq("error.validation.failed"), any(), any(Locale.class))).thenReturn("Validation failed.");
        
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(patch("/modules/{id}", moduleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.title").value("Title cannot be blank."));
        
        verify(moduleService).findById(moduleId);
        verify(moduleService, never()).save(any(Module.class)); // Should not reach save layer
    }
    
    @Test
    void updateModule_notFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long moduleId = 1L;
        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setTitle("Updated Title");
        
        doNothing().when(authorizationService).authorize(any(), any(), any(HttpServletRequest.class));
        when(moduleService.findById(moduleId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Module not found");
        
        String reqBody = objectMapper.writeValueAsString(moduleDTO);
        
        // Act & Assert
        mockMvc.perform(patch("/modules/{id}", moduleId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Module not found"));
        
        verify(moduleService).findById(moduleId);
        verify(authorizationService, never()).authorize(any(), any(), any()); // Should not reach auth service
    }
    
    @Test
    void deleteModule_existingModule_shouldReturnNoContent() throws Exception {
        // Arrange
        long moduleId = 1L;
        Module module = new Module();
        module.setId(moduleId);
        module.setDeleted(false);
        
        when(moduleService.findById(moduleId)).thenReturn(Optional.of(module));
        doNothing().when(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(module), any(HttpServletRequest.class));
        doAnswer(invocation -> {
            module.setDeleted(true); // Simulate the soft delete
            return null;
        }).when(moduleService).deleteById(moduleId); // Assuming this method is void
        
        // Act & Assert
        mockMvc.perform(delete("/modules/{id}", moduleId))
                .andExpect(status().isNoContent());
        
        // Verify interactions
        verify(moduleService).findById(moduleId);
        verify(authorizationService).authorize(eq(AuthorizationLevel.OWNER), eq(module), any(HttpServletRequest.class));
        verify(moduleService).deleteById(moduleId);
        
        // Assert the module is marked as deleted
        assertTrue(module.isDeleted(), "Module should be marked as deleted");
    }
    
    
    @Test
    void deleteModule_notFound_shouldReturnNotFoundStatus() throws Exception {
        // Arrange
        long moduleId = 1L;
        when(moduleService.findById(moduleId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Module not found");
        
        // Act & Assert
        mockMvc.perform(delete("/modules/{id}", moduleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Module not found"));
        
        verify(moduleService).findById(moduleId);
        verify(authorizationService, never()).authorize(any(), any(), any()); // Should not reach auth service
    }
}