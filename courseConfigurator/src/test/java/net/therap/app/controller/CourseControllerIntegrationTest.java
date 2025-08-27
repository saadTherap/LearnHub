//package net.therap.app.controller;
//
//import net.therap.app.dto.CourseCatalogDTO;
//import net.therap.app.helper.DtoHelper;
//import net.therap.app.model.Course;
//import net.therap.app.service.*;
//import net.therap.cache.support.HazelcastCacheService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.context.MessageSource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author gazizafor
// * @since 27/8/25
// */
//@WebMvcTest(CourseController.class)
//class CourseControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private CourseService courseService;
//
//    @MockitoBean
//    private HazelcastCacheService hazelcastCacheService;
//
//    @MockitoBean
//    private AuthorizationService authorizationService;
//
//    @MockitoBean
//    private DtoHelper dtoHelper;
//
//    @MockitoBean
//    private net.therap.app.mapper.CourseMapper courseMapper;
//
//    @MockitoBean
//    private InstructorService instructorService;
//
//    @MockitoBean
//    private ContentService contentService;
//
//    @MockitoBean
//    private ModuleService moduleService;
//
//    @MockitoBean
//    private MessageSource messageSource;
//
//
//    @Test
//    void getCourseById_courseFound_shouldReturnOk() throws Exception {
//        // Arrange
//        long courseId = 1L;
//        Course course = new Course();
//        course.setId(courseId);
//        when(courseService.findById(courseId)).thenReturn(Optional.of(course));
//        when(dtoHelper.toDetailedCourseCatalogDTO(any(Course.class))).thenReturn(new CourseCatalogDTO());
//
//        // Act & Assert
//        mockMvc.perform(get("/courses/{id}", courseId))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getCourseById_courseNotFound_shouldReturnNotFound() throws Exception {
//        // Arrange
//        long courseId = 1L;
//        when(courseService.findById(courseId)).thenReturn(Optional.empty());
//        when(hazelcastCacheService.get(anyString(), anyLong())).thenReturn(null); // Updated to handle anyLong()
//
//        // Act & Assert
//        mockMvc.perform(get("/courses/{id}", courseId))
//                .andExpect(status().isNotFound());
//    }
//}
