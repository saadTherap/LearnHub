package net.therap.secureFileServer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.mapper.StoredFileMapper;
import net.therap.secureFileServer.service.FileStorageService;
import net.therap.secureFileServer.validator.FileValidator;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author avidewan
 * @since 7/22/25
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File API", description = "Endpoints for uploading, downloading, listing and deleting files")
public class FileController {

    private final FileStorageService fileService;
    private final StoredFileMapper fileMapper;
    private final FileValidator fileValidator;

    public FileController(FileStorageService fileService,
                          StoredFileMapper fileMapper,
                          FileValidator fileValidator) {

        this.fileService = fileService;
        this.fileMapper = fileMapper;
        this.fileValidator = fileValidator;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    public ResponseEntity<StoredFileDto> uploadFile(
            @Parameter(description = "The file to upload", required = true)
            @RequestParam("file") MultipartFile file) {

        fileValidator.validate(file);

        StoredFile storedFile = fileService.saveFile(file);
        StoredFileDto dto = fileMapper.toDto(storedFile);

        return ResponseEntity.created(URI.create(dto.getDownloadUrl())).body(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download a file by ID")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "ID of the file to download", required = true)
            @PathVariable Long id) {

        StoredFile storedFile = fileService.getMetadata(id);
        Resource resource = fileService.loadFileAsResource(id);

        String encodedFilename = URLEncoder.encode(storedFile.getOriginalFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(storedFile.getContentType())).
                header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename).
                body(resource);
    }

    @GetMapping
    @Operation(summary = "List all uploaded files")
    public ResponseEntity<List<StoredFileDto>> listFiles() {
        List<StoredFile> files = fileService.getAllFiles();

        List<StoredFileDto> storedFileDtos = files.stream().map(fileMapper::toDto).toList();

        return ResponseEntity.ok(storedFileDtos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a file by ID")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "ID of the file to delete", required = true)
            @PathVariable Long id) {

        fileService.deleteFile(id);

        return ResponseEntity.noContent().build();
    }
}