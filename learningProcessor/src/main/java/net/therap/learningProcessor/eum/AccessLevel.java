package net.therap.learningProcessor.eum;

/**
 * @author avidewan
 * @since 8/13/25
 */
public enum AccessLevel {

    PUBLIC,

    STUDENT_ONLY,

    STUDENT_WITH_ID,

    STUDENT_ENROLLED_IN_COURSE,

    INSTRUCTOR_ONLY,

    INSTRUCTOR_OF_COURSE,

    INSTRUCTOR_OR_STUDENT_WITH_ID,

    INSTRUCTOR_OR_STUDENT_ENROLLED_IN_COURSE,

    INSTRUCTOR_OF_COURSE_OR_STUDENT_ENROLLED_IN_COURSE,
}