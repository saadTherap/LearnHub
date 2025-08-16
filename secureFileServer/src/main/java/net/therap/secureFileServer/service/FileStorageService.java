package net.therap.secureFileServer.service;

import lombok.extern.slf4j.Slf4j;
import net.therap.secureFileServer.config.StorageProperties;
import net.therap.secureFileServer.dto.FileMetaDataDto;
import net.therap.secureFileServer.entity.primary.StoredFile;
import net.therap.secureFileServer.exception.FileNotFoundException;
import net.therap.secureFileServer.repository.primary.FileRepository;
import net.therap.secureFileServer.util.MessageUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author avidewan
 * @since 7/22/25
 */
@Service
@Slf4j
public class FileStorageService {

    private final Path storagePath;
    private final FileRepository fileRepository;
    private final MessageUtil messageUtil;

    public FileStorageService(FileRepository fileRepository,
                              StorageProperties storageProperties,
                              MessageUtil messageUtil) throws IOException {

        this.fileRepository = fileRepository;
        this.messageUtil = messageUtil;
        this.storagePath = Paths.get(storageProperties.getUploadDir());

        Files.createDirectories(storagePath);
    }

    public StoredFile saveFile(MultipartFile multipartFile,
                               FileMetaDataDto uploaderDto) {

        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + extension;

        Path targetPath = storagePath.resolve(storedFilename);

        log.info("Attempting to store file: original='{}', size={} bytes, userId={}, role={}, context={}",
                multipartFile.getOriginalFilename(),
                multipartFile.getSize(),
                uploaderDto.getUserId(),
                uploaderDto.getUserRole(),
                uploaderDto.getContextId());

        try {
            Files.copy(multipartFile.getInputStream(), targetPath);
            log.debug("File successfully written to {}", targetPath);

        } catch (IOException e) {
            throw new RuntimeException(messageUtil.getMessage("error.file.store-failed", multipartFile.getOriginalFilename()), e);
        }

        StoredFile storedFile = mapToStoredFile(multipartFile, storedFilename, uploaderDto);

        log.info("File successfully stored: id={}, storedName='{}', uploaderId={}, role={}, context={}",
                storedFile.getId(),
                storedFile.getStoredFilename(),
                storedFile.getUploaderId(),
                storedFile.getUploaderRole(),
                storedFile.getContextId());

        return fileRepository.save(storedFile);
    }

    public Resource loadFileAsResource(Long id) {
        log.info("Loading file resource with id={}", id);

        StoredFile file = getMetadata(id);
        Path path = storagePath.resolve(file.getStoredFilename());
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not readable or missing: id={}, storedName={}", id, file.getStoredFilename());

                throw new FileNotFoundException(messageUtil.getMessage("error.file.not-readable", file.getStoredFilename()));
            }

        } catch (IOException e) {
            log.error("Error reading file: id={}, storedName={}", id, file.getStoredFilename(), e);

            throw new RuntimeException(messageUtil.getMessage("error.file.read-failed", file.getStoredFilename()), e);
        }

        log.info("File successfully loaded: id={}, storedName={}, uploaderId={}, role={}",
                id, file.getStoredFilename(), file.getUploaderId(), file.getUploaderRole());

        return resource;
    }

    public StoredFile getMetadata(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(messageUtil.getMessage("error.file.not-found", id)));
    }

    public List<StoredFile> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(Long id) {
        StoredFile file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(messageUtil.getMessage("error.file.not-found", id)));

        try {
            Files.deleteIfExists(storagePath.resolve(file.getStoredFilename()));

        } catch (IOException e) {
            throw new RuntimeException(messageUtil.getMessage("error.file.delete-failed", file.getStoredFilename()), e);
        }

        fileRepository.deleteById(id);
    }

    private StoredFile mapToStoredFile(MultipartFile multipartFile,
                                       String storedFilename,
                                       FileMetaDataDto uploaderDto) {

        StoredFile storedFile = new StoredFile();

        storedFile.setOriginalFilename(multipartFile.getOriginalFilename());
        storedFile.setStoredFilename(storedFilename);
        storedFile.setContentType(multipartFile.getContentType());
        storedFile.setUploadTime(LocalDateTime.now());

        storedFile.setUploaderId(uploaderDto.getUserId());
        storedFile.setUploaderRole(uploaderDto.getUserRole());
        storedFile.setContextId(uploaderDto.getContextId());

        return storedFile;
    }

    private String getFileExtension(String filename) {
        if (Objects.nonNull(filename) && filename.contains(".")) {

            return filename.substring(filename.lastIndexOf("."));
        }

        return "";
    }
}