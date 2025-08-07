package net.therap.learningProcessor.client;

import net.therap.learningProcessor.dto.*;
import net.therap.learningProcessor.dto.content.BaseContentDto;
import net.therap.learningProcessor.dto.content.ContentDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


/**
 * @author avidewan
 * @since 7/27/25
 */
@Component
public class CourseClient {

    private static final String SERVICE_NAME = "course-configurator";
    private static final String BASE_PATH = "/course-configurator/api";

    @Autowired
    private ServiceDiscoveryCache serviceDiscoveryCache;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getServiceBaseUrl() {
        Map<String, Object> instance = serviceDiscoveryCache.getInstance(SERVICE_NAME);
        String host = (String) instance.get("host");
        Integer port = (Integer) instance.get("port");
        return "http://" + host + ":" + port + BASE_PATH;
    }

    public List<CourseCatalogDto> getAllCourseCatalogs() {
        String url = getServiceBaseUrl() + "/courses/public";
        return restTemplate.getForObject(url, List.class);
    }

    public CourseCatalogDto getCourseCatalog(Long courseId) {
        String url = getServiceBaseUrl() + "/courses/public/" + courseId;
        return restTemplate.getForObject(url, CourseCatalogDto.class);
    }

    public CourseDetailWithProgressDto getCourseDetail(Long courseId) {
        String url = getServiceBaseUrl() + "/courses/" + courseId;
        return restTemplate.getForObject(url, CourseDetailWithProgressDto.class);
    }

    public List<ModuleWithProgressDto> getModulesByCourse(Long courseId) {
        String url = getServiceBaseUrl() + "/modules/byCourse/" + courseId;
        return restTemplate.getForObject(url, List.class);
    }

    public List<BaseContentDto> getContentsByModule(Long moduleId) {
        String url = getServiceBaseUrl() + "/contents/byModule/" + moduleId;
        return restTemplate.getForObject(url, List.class);
    }

    public ContentDetailDto getContentDetail(Long contentId) {
        String url = getServiceBaseUrl() + "/contents/detail/" + contentId;
        return restTemplate.getForObject(url, ContentDetailDto.class);
    }
}
