package net.therap.secureFileServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.primary.StoredFile;
import net.therap.secureFileServer.entity.course.Course;
import net.therap.secureFileServer.exception.InvalidFileSignatureException;
import net.therap.secureFileServer.mapper.StoredFileMapper;
import net.therap.secureFileServer.repository.course.CourseRepository;
import net.therap.secureFileServer.service.FileSignatureService;
import net.therap.secureFileServer.service.FileStorageService;
import net.therap.secureFileServer.util.MessageUtil;
import net.therap.secureFileServer.validator.FileValidator;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author avidewan
 * @since 7/22/25
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File API", description = "Endpoints for uploading, downloading, listing and deleting files")
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;
    private final FileSignatureService fileSignatureService;
    private final StoredFileMapper fileMapper;
    private final FileValidator fileValidator;
    
    private final CourseRepository courseRepository;

    private final MessageUtil messageUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    public ResponseEntity<StoredFileDto> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam Map<String, String> contextMap
    ) throws IOException {

        String uploaderEmail = contextMap.get("uploaderEmail");

        if (uploaderEmail == null || uploaderEmail.isBlank()) {
            throw new IllegalArgumentException("uploaderEmail is required in contextMap");
        }

        log.info("File upload requested: original='{}', size={} bytes, uploaderEmail={}",
                file.getOriginalFilename(), file.getSize(), uploaderEmail);

        fileValidator.validate(file);

        StoredFile storedFile = fileStorageService.saveFile(file, uploaderEmail);

        StoredFileDto dto = fileMapper.toDto(storedFile);

        log.info("File upload successful: id={}, storedName='{}', uploaderEmail={}",
                storedFile.getId(), storedFile.getStoredFilename(), uploaderEmail);

        return ResponseEntity.created(URI.create(dto.getDownloadUrl())).body(dto);
    }

    @GetMapping("/download")
    @Operation(summary = "Download a file by formId with optional signature")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("formId") String formId,
            @RequestParam(value = "signature") String signature) {

        log.info("Download request: formId={}, signatureProvided={}", formId, (signature != null));

        StoredFile storedFile = fileStorageService.getByFormId(formId);

        if (signature == null || !fileSignatureService.verifySignature(storedFile, signature)) {
            log.warn("Download denied due to invalid/missing signature: formId={}", formId);

            throw new InvalidFileSignatureException(messageUtil.getMessage("error.invalid-signature.message"));
        }

        Resource resource = fileStorageService.loadFileAsResource(storedFile.getId());
        String encodedFilename = URLEncoder.encode(storedFile.getOriginalFilename(), StandardCharsets.UTF_8);

        log.info("File download approved: formId={}, original='{}', stored='{}'",
                storedFile.getFormId(),
                storedFile.getOriginalFilename(),
                storedFile.getStoredFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    @GetMapping
    @Operation(summary = "List all uploaded files")
    public ResponseEntity<List<StoredFileDto>> listFiles() {
        log.info("Listing all uploaded files");

        List<StoredFile> files = fileStorageService.getAllFiles();

        List<StoredFileDto> storedFileDtos = files.stream().map(fileMapper::toDto).toList();

        log.info("Files listed: count={}", storedFileDtos.size());

        return ResponseEntity.ok(storedFileDtos);
    }

    @DeleteMapping("/hard/{formId}")
    @Operation(summary = "Delete a file by formId")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "formId of the file to delete", required = true)
            @PathVariable String formId) {

        log.info("File delete requested: formId={}", formId);

        fileStorageService.deleteByFormId(formId);

        log.info("File delete successful: formId={}", formId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return ResponseEntity.ok(courses);
    }
}