package net.therap.secureFileServer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author avidewan
 * @since 7/22/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "avi_stored_file")
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stored_file_seq_gen")
    @SequenceGenerator(
            name = "stored_file_seq_gen",
            sequenceName = "avi_stored_file_seq",
            allocationSize = 1
    )
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "original_file_name")
    private String originalFilename;

    @Column(name = "stored_file_name")
    private String storedFilename;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;
}