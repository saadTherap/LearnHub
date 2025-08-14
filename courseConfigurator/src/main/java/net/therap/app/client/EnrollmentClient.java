package net.therap.app.client;

import net.therap.app.dto.StudentDTO;
import net.therap.app.dto.StudentProgressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Component
public class EnrollmentClient {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentClient.class);
    private static final String SERVICE_NAME = "learning-processor";
    private static final String BASE_PATH = "/api/learning-processor/student-course";

    @Autowired
    private ServiceDiscoveryCache serviceDiscoveryCache;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getServiceBaseUrl() {
        Map<String, Object> instance = serviceDiscoveryCache.getInstance(SERVICE_NAME);
        String host = (String) instance.get("host");
        Integer port = (Integer) instance.get("port");
        return "http://" + host + ":" + port + BASE_PATH;
    }

    public List<StudentDTO> getAllStudentsOfCourseDetailed(long courseId) {
        try {
            String url = getServiceBaseUrl() + "/enrollments/course/" + courseId;
            return restTemplate.getForObject(url, List.class);
        } catch (ResourceAccessException e) {
            logger.error("Failed to get student details for course {}. Learning Processor service is unavailable.", courseId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching student details for course {}.", courseId, e);
            throw new RuntimeException("Failed to fetch student details.", e);
        }
    }

    public List<Long> getAllStudentsOfCourse(long courseId) {
        try {
            String url = getServiceBaseUrl() + "/enrollments/course/" + courseId + "/studentIds";
            return restTemplate.getForObject(url, List.class);
        } catch (ResourceAccessException e) {
            logger.error("Failed to get student IDs for course {}. Learning Processor service is unavailable.", courseId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching student IDs for course {}.", courseId, e);
            throw new RuntimeException("Failed to fetch student IDs.", e);
        }
    }

    public List<StudentProgressDTO> getProgressByStudentAndCourse(long studentId, long courseId) {
        try {
            String url = getServiceBaseUrl() + "/progress/" + studentId + "/" + courseId;
            return restTemplate.getForObject(url, List.class);
        } catch (ResourceAccessException e) {
            logger.error("Failed to get progress for student {} in course {}. Learning Processor service is unavailable.", studentId, courseId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching student progress for student {} in course {}.", studentId, courseId, e);
            throw new RuntimeException("Failed to fetch student progress.", e);
        }
    }

    public List<StudentProgressDTO> getProgressByCourse(long courseId) {
        try {
            String url = getServiceBaseUrl() + "/progress/course/" + courseId;
            return restTemplate.getForObject(url, List.class);
        } catch (ResourceAccessException e) {
            logger.error("Failed to get progress for course {}. Learning Processor service is unavailable.", courseId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching student progress for course {}.", courseId, e);
            throw new RuntimeException("Failed to fetch student progress.", e);
        }
    }
}
