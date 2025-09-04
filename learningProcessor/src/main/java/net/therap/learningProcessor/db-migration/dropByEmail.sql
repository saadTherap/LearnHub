DECLARE
    v_student_id NUMBER(19);
BEGIN
    -- Find the student ID by email (replace manually as needed)
    SELECT id
    INTO v_student_id
    FROM avi_student
    WHERE email = 'TestStudent25@gmail.com';

    -- Delete submissions
    DELETE FROM avi_student_submission
    WHERE student_id = v_student_id;

    -- Delete content completions
    DELETE FROM avi_student_content_completion
    WHERE student_id = v_student_id;

    -- Delete enrollments
    DELETE FROM avi_course_enrollment
    WHERE student_id = v_student_id;

    -- Finally, delete student
--     DELETE FROM avi_student
--     WHERE id = v_student_id;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No student found with that email.');
END;
/
