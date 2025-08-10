package net.therap.app.client;

import net.therap.app.dto.StudentDTO;
import net.therap.app.dto.StudentProgressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author gazizafor
 * @since 4/8/25
 */
@Component
public class EnrollmentClient {

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
        String url = getServiceBaseUrl() + "/enrollments/course/" + courseId;
        return restTemplate.getForObject(url, List.class);
    }

    public List<Long> getAllStudentsOfCourse(long courseId) {
        String url = getServiceBaseUrl() + "/enrollments/course/" + courseId + "/studentIds";
        return restTemplate.getForObject(url, List.class);
    }

    public List<StudentProgressDTO> getProgressByStudentAndCourse(long studentId, long courseId) {
        String url = getServiceBaseUrl() + "/progress/" + studentId + "/" + courseId;
        return restTemplate.getForObject(url, List.class);
    }

    public List<StudentProgressDTO> getProgressByCourse(long courseId) {
        String url = getServiceBaseUrl() + "/progress/course/" + courseId;
        return restTemplate.getForObject(url, List.class);
    }
}
