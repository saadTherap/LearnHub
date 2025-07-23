package net.therap.secureFileServer.controller;

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

    @PostMapping
    public ResponseEntity<StoredFileDto> uploadFile(@RequestParam("file") MultipartFile file) {
        fileValidator.validate(file);

        StoredFile storedFile = fileService.saveFile(file);
        StoredFileDto dto = fileMapper.toDto(storedFile);

        return ResponseEntity.created(URI.create(dto.getDownloadUrl())).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
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
    public ResponseEntity<List<StoredFileDto>> listFiles() {
        List<StoredFile> files = fileService.getAllFiles();

        List<StoredFileDto> storedFileDtos = files.stream().map(fileMapper::toDto).toList();

        return ResponseEntity.ok(storedFileDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);

        return ResponseEntity.noContent().build();
    }
}