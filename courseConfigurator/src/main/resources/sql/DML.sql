-- =====================================================================
-- DML for NEW Courses
-- =====================================================================

-- Course 3: Data Structures & Algorithms (Instructor 1: Alice Wonderland)
INSERT INTO final_learnhub_course (id, name, description, current_release, instructor_id, created_at, updated_at, version, is_deleted)
VALUES (3, 'Data Structures & Algorithms', 'Master essential data structures and algorithms for competitive programming and interviews.', 1, 1, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

-- Course 4: Introduction to Database Systems (Instructor 2: Bob The Builder)
INSERT INTO final_learnhub_course (id, name, description, current_release, instructor_id, created_at, updated_at, version, is_deleted)
VALUES (4, 'Introduction to Database Systems', 'Learn the fundamentals of relational databases, SQL, and database design.', 1, 2, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);


-- =====================================================================
-- DML for NEW Modules
-- =====================================================================

-- Modules for Course 3: Data Structures & Algorithms (Course ID 3)
INSERT INTO final_learnhub_module (id, title, course_id, created_at, updated_at, version, is_deleted)
VALUES (3, 'Arrays & Linked Lists', 3, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

INSERT INTO final_learnhub_module (id, title, course_id, created_at, updated_at, version, is_deleted)
VALUES (4, 'Trees & Graphs', 3, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

-- Modules for Course 4: Introduction to Database Systems (Course ID 4)
INSERT INTO final_learnhub_module (id, title, course_id, created_at, updated_at, version, is_deleted)
VALUES (5, 'Relational Model & SQL', 4, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

INSERT INTO final_learnhub_module (id, title, course_id, created_at, updated_at, version, is_deleted)
VALUES (6, 'Database Design', 4, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);


-- =====================================================================
-- DML for NEW Content (Logical Content Units)
-- Linking to new modules
-- =====================================================================

-- Content for Module 3: Arrays & Linked Lists (Module ID 3)
INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1005, 3, 'Array Basics', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Lecture

INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1006, 3, 'Linked List Concepts', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Lecture

-- Content for Module 4: Trees & Graphs (Module ID 4)
INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1007, 4, 'Tree Traversal Quiz', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Quiz

-- Content for Module 5: Relational Model & SQL (Module ID 5)
INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1008, 5, 'SQL Queries Assignment', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Submission

INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1009, 5, 'Normalization Forms', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Lecture

-- Content for Module 6: Database Design (Module ID 6)
INSERT INTO final_learnhub_content (id, module_id, title, current_content_release_id, created_at, updated_at, version, is_deleted)
VALUES (1010, 6, 'ER Diagramming Practice', NULL, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0); -- Submission


-- =====================================================================
-- DML for NEW Content_Release (Specific Content Versions)
-- Linking to new logical content units
-- =====================================================================

-- Content Release 6: Lecture (1005, Release 1) - Array Basics
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (6, 1005, 1, NULL, 0, 'LECTURE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

-- Content Release 7: Lecture (1006, Release 1) - Linked List Concepts
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (7, 1006, 1, NULL, 0, 'LECTURE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

-- Content Release 8: Quiz (1007, Release 1) - Tree Traversal Quiz
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (8, 1007, 1, NULL, 0, 'QUIZ', SYSTIMESTAMP, SYSTIMESTAMP, 1);

-- Content Release 9: Submission (1008, Release 1) - SQL Queries Assignment
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (9, 1008, 1, NULL, 0, 'SUBMISSION', SYSTIMESTAMP, SYSTIMESTAMP, 1);

-- Content Release 10: Lecture (1009, Release 1) - Normalization Forms
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (10, 1009, 1, NULL, 0, 'LECTURE', SYSTIMESTAMP, SYSTIMESTAMP, 1);

-- Content Release 11: Submission (1010, Release 1) - ER Diagramming Practice
INSERT INTO final_learnhub_content_release (id, content_id, "release", release_end, is_deleted, content_type, created_at, updated_at, version)
VALUES (11, 1010, 1, NULL, 0, 'SUBMISSION', SYSTIMESTAMP, SYSTIMESTAMP, 1);


-- =====================================================================
-- DML for Specific Content Details (Lecture, Quiz, Submission)
-- Linking to new Content_Release IDs
-- =====================================================================

-- Lecture details for Content Release 6 (ID 6)
INSERT INTO final_learnhub_lecture (id, description, video_url, resource_link)
VALUES (6, 'Introduction to arrays and their basic operations.', 'https://youtube.com/array_basics', 'https://slides.com/array_basics');

-- Lecture details for Content Release 7 (ID 7)
INSERT INTO final_learnhub_lecture (id, description, video_url, resource_link)
VALUES (7, 'Understanding singly and doubly linked lists.', 'https://youtube.com/linked_lists', 'https://slides.com/linked_lists');

-- Quiz details for Content Release 8 (ID 8)
INSERT INTO final_learnhub_quiz (id)
VALUES (8);

-- Submission details for Content Release 9 (ID 9)
INSERT INTO final_learnhub_submission (id, description, resource_link)
VALUES (9, 'Write complex SQL queries for given scenarios.', 'https://github.com/sql_assignment');

-- Lecture details for Content Release 10 (ID 10)
INSERT INTO final_learnhub_lecture (id, description, video_url, resource_link)
VALUES (10, 'Detailed explanation of 1NF, 2NF, 3NF, and BCNF.', 'https://youtube.com/normalization', 'https://slides.com/normalization');

-- Submission details for Content Release 11 (ID 11)
INSERT INTO final_learnhub_submission (id, description, resource_link)
VALUES (11, 'Design an ER diagram for a library management system.', 'https://github.com/er_diagram_practice');


-- =====================================================================
-- DML for NEW Quiz Questions and Options
-- Linking to new quiz_release_id (Content Release 8)
-- =====================================================================

-- Question 3 for Quiz Release 8 (ID 8)
INSERT INTO final_learnhub_quiz_question (id, quiz_release_id, question_text, created_at, updated_at, version, is_deleted)
VALUES (3, 8, 'Which data structure uses LIFO principle?', SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

-- Options for Question 3 (ID 3)
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (6, 3, 'Queue', 0, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (7, 3, 'Stack', 1, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (8, 3, 'Linked List', 0, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

-- Question 4 for Quiz Release 8 (ID 8)
INSERT INTO final_learnhub_quiz_question (id, quiz_release_id, question_text, created_at, updated_at, version, is_deleted)
VALUES (4, 8, 'What is the time complexity of searching in a hash table (average case)?', SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);

-- Options for Question 4 (ID 4)
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (9, 4, 'O(n)', 0, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (10, 4, 'O(log n)', 0, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);
INSERT INTO final_learnhub_quiz_option (id, question_id, option_text, is_correct, created_at, updated_at, version, is_deleted)
VALUES (11, 4, 'O(1)', 1, SYSTIMESTAMP, SYSTIMESTAMP, 1, 0);


-- =====================================================================
-- FINAL UPDATE: Link Logical Content to its Current Active Release
-- For the NEW Content Units
-- =====================================================================

UPDATE final_learnhub_content
SET current_content_release_id = 6
WHERE id = 1005;

UPDATE final_learnhub_content
SET current_content_release_id = 7
WHERE id = 1006;

UPDATE final_learnhub_content
SET current_content_release_id = 8
WHERE id = 1007;

UPDATE final_learnhub_content
SET current_content_release_id = 9
WHERE id = 1008;

UPDATE final_learnhub_content
SET current_content_release_id = 10
WHERE id = 1009;

UPDATE final_learnhub_content
SET current_content_release_id = 11
WHERE id = 1010;
