package net.therap.secureFileServer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author avidewan
 * @since 8/16/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseSummaryDto {
    private long courseId;
    private long instructorId;
}