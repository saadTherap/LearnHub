package net.therap.learningProcessor.eum;

/**
 * @author avidewan
 * @since 8/13/25
 */
public enum AccessLevel {

    PUBLIC,

    TEACHER_ONLY,
    TEACHER_OF_COURSE,

    STUDENT_WITH_ID,
    STUDENT_ENROLLED_IN_COURSE,

    TEACHER_AND_STUDENT_WITH_ID,
    TEACHER_OF_COURSE_OR_STUDENT_WITH_ID
}

