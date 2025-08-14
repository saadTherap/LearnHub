//package net.therap.app.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import net.therap.app.model.Instructor;
//import net.therap.app.service.CourseService;
//import net.therap.app.service.InstructorService;
//import net.therap.auth.context.UserRequestCache;
//import net.therap.auth.util.AuthDataUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
///**
// * @author gazizafor
// * @since 13/8/25
// */
//public class AuthorizationInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private CourseService courseService;
//
//    @Autowired
//    private InstructorService instructorService;
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // Assume you have a way to get the current user's ID and role from the security context
//        long userId = Long.parseLong(request.getParameter("userId"));
//        UserRequestCache.UserInfo userInfo = AuthDataUtil.getUserInfo(userId);
//        assert userInfo != null;
//        String email = userInfo.email();
//        String currentUserRole = userInfo.role();
//
//        Optional<Instructor> instructorOptional = instructorService.getInstructorById(userId);
//
//        if (instructorOptional.isEmpty()) {
//            throw new NoSuchElementException(messageSource.getRole("not.found.instructor", null, request.getLocale()));
//        }
//
//        if ("ADMIN".equals(currentUserRole)) {
//            return true;
//        }
//
//        // Example for role-based access
//        if (request.getRequestURI().endsWith("/admin/dashboard") && !"ADMIN".equals(currentUserRole)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
//            return false;
//        }
//
//        // --- ID-based access for courses ---
//        if (request.getRequestURI().matches("/api/courses/\\d+/.*")) {
//            Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//            if (pathVariables != null && pathVariables.containsKey("courseId")) {
//                long courseId = Long.parseLong((String) pathVariables.get("courseId"));
//
//                // Use a service method to check ownership
////                if (!courseService.isCourseOwner(courseId, currentUserId)) {
////                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You are not the owner of this course");
////                    return false;
////                }
//            }
//        }
//
//        return true;
//    }
//}