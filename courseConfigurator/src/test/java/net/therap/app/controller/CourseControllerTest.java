package net.therap.app.controller;

import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.CourseCatalogDTO;
import net.therap.app.dto.CourseDTO;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.CourseMapper;
import net.therap.app.model.Course;
import net.therap.app.service.CourseService;
import net.therap.cache.support.HazelcastCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * @author riadanonto
 * @since 13/8/25
 */
@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock private CourseMapper courseMapper;
    @Mock private CourseService courseService;
    @Mock private DtoHelper dtoHelper;
    @Mock private HazelcastCacheService hazelcastCacheService;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setup() {
        // No Spring context — just your controller and mocks
        this.mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .build();
    }

    @Test
    void testGetAllCourses() throws Exception {
        Course course = new Course();
        CourseCatalogDTO dto = new CourseCatalogDTO();

        when(courseService.findAll()).thenReturn(List.of(course));
        when(dtoHelper.toDetailedCourseCatalogDTO(any(Course.class))).thenReturn(dto);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // just check there's at least one element
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testGetCourseById_fromCache() throws Exception {
        long courseId = 1L;
        CourseCatalogDTO cachedDto = new CourseCatalogDTO();

        when(hazelcastCacheService.get(CacheConstants.COURSE_CATALOG, courseId))
                .thenReturn(cachedDto);

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateCourse() throws Exception {
        // Valid request JSON (avoid validation 400)
        String requestJson = """
            {
              "name": "New Course",
              "description": "Some description"
            }
        """;

        CourseDTO requestDto = new CourseDTO();
        requestDto.setName("New Course");
        requestDto.setDescription("Some description");

        Course mapped = new Course(); // what mapper would create
        mapped.setName("New Course");
        mapped.setDescription("Some description");

        Course saved = new Course();
        saved.setId(1L);
        saved.setName("New Course");
        saved.setDescription("Some description");

        CourseDTO responseDto = new CourseDTO();
        responseDto.setId(1L);
        responseDto.setName("New Course");
        responseDto.setDescription("Some description");

        when(courseMapper.toCourse(any(CourseDTO.class))).thenReturn(mapped);
        when(courseService.save(any(Course.class))).thenReturn(saved);
        when(dtoHelper.toCourseDTO(saved)).thenReturn(responseDto);

        mockMvc.perform(post("/courses/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteCourse_success() throws Exception {
        long courseId = 1L;

        when(courseService.findById(courseId)).thenReturn(Optional.of(new Course()));
        when(courseService.deleteById(courseId)).thenReturn(new Course());

        mockMvc.perform(delete("/courses/{id}", courseId))
                .andExpect(status().isNoContent());

        verify(courseService).deleteById(courseId);
    }
}
