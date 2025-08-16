package net.therap.secureFileServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.secureFileServer.dto.FileMetaDataDto;
import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.entity.course.Course;
import net.therap.secureFileServer.exception.FileAccessDeniedException;
import net.therap.secureFileServer.exception.InvalidFileSignatureException;
import net.therap.secureFileServer.mapper.StoredFileMapper;
import net.therap.secureFileServer.repository.course.CourseRepository;
import net.therap.secureFileServer.service.FileAuthorizationService;
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
    
    private final FileAuthorizationService fileAuthorizationService;

    private final CourseRepository courseRepository;

    private final MessageUtil messageUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    public ResponseEntity<StoredFileDto> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("userRole") String userRole,
            @RequestParam("contextId") Long contextId) throws IOException {

        log.info("File upload requested: original='{}', size={} bytes, userId={}, role={}, context={}",
                file.getOriginalFilename(),
                file.getSize(),
                userId,
                userRole,
                contextId);

        fileValidator.validate(file);

        FileMetaDataDto uploaderDto = new FileMetaDataDto(userId, userRole, contextId);

        StoredFile storedFile = fileStorageService.saveFile(file, uploaderDto);
        StoredFileDto dto = fileMapper.toDto(storedFile);

        String signature = fileSignatureService.generateSignature(storedFile);
        dto.setSignature(signature);

        log.info("File upload successful: id={}, storedName='{}', userId={}, role={}, context={}",
                storedFile.getId(),
                storedFile.getStoredFilename(),
                storedFile.getUploaderId(),
                storedFile.getUploaderRole(),
                storedFile.getContextId());

        return ResponseEntity.created(URI.create(dto.getDownloadUrl())).body(dto);
    }

    @PostMapping("/{id}/signature")
    @Operation(summary = "Get HMAC signature to download a file")
    public ResponseEntity<String> getDownloadSignature(
            @PathVariable Long id,
            @RequestBody FileMetaDataDto requestData) {

        log.info("Signature request: fileId={}, userId={}, role={}",
                id, requestData.getUserId(), requestData.getUserRole());

        StoredFile storedFile = fileStorageService.getMetadata(id);

        if(!fileAuthorizationService.canAccessFile(storedFile,
                requestData.getUserId(),
                requestData.getUserRole())) {

            log.warn("Access denied for signature: fileId={}, userId={}, role={}",
                    id, requestData.getUserId(), requestData.getUserRole());

            throw new FileAccessDeniedException(messageUtil.getMessage("error.access-denied.message"));
        }

        String signature = fileSignatureService.generateSignature(storedFile);

        log.info("Signature generated successfully: fileId={}, userId={}, role={}",
                id, requestData.getUserId(), requestData.getUserRole());

        return ResponseEntity.ok(signature);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download a file by ID with optional signature")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @RequestParam(value = "signature") String signature) {

        log.info("Download request: fileId={}, signatureProvided={}", id, (signature != null));

        StoredFile storedFile = fileStorageService.getMetadata(id);

        if (signature == null || !fileSignatureService.verifySignature(storedFile, signature)) {
            log.warn("Download denied due to invalid/missing signature: fileId={}", id);

            throw new InvalidFileSignatureException(messageUtil.getMessage("error.invalid-signature.message"));
        }

        Resource resource = fileStorageService.loadFileAsResource(id);
        String encodedFilename = URLEncoder.encode(storedFile.getOriginalFilename(), StandardCharsets.UTF_8);

        log.info("File download approved: id={}, original='{}', stored='{}'",
                storedFile.getId(),
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

    @DeleteMapping("/hard/{id}")
    @Operation(summary = "Delete a file by ID")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "ID of the file to delete", required = true)
            @PathVariable Long id) {

        log.info("File delete requested: id={}", id);

        fileStorageService.deleteFile(id);

        log.info("File delete successful: id={}", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return ResponseEntity.ok(courses);
    }
}