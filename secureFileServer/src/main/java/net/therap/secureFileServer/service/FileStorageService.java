package net.therap.secureFileServer.service;

import net.therap.secureFileServer.config.StorageProperties;
import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.exception.FileNotFoundException;
import net.therap.secureFileServer.repository.FileRepository;
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

    public StoredFile saveFile(MultipartFile multipartFile) {
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + extension;

        Path targetPath = storagePath.resolve(storedFilename);

        try {
            Files.copy(multipartFile.getInputStream(), targetPath);

        } catch (IOException e) {
            throw new RuntimeException(messageUtil.getMessage("error.file.store-failed", multipartFile.getOriginalFilename()), e);
        }

        StoredFile storedFile = mapToStoredFile(multipartFile, storedFilename);

        return fileRepository.save(storedFile);
    }

    public Resource loadFileAsResource(Long id) {
        StoredFile file = getMetadata(id);
        Path path = storagePath.resolve(file.getStoredFilename());
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException(messageUtil.getMessage("error.file.not-readable", file.getStoredFilename()));
            }

        } catch (IOException e) {
            throw new RuntimeException(messageUtil.getMessage("error.file.read-failed", file.getStoredFilename()), e);
        }

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

    private StoredFile mapToStoredFile(MultipartFile multipartFile, String storedFilename) {
        StoredFile storedFile = new StoredFile();

        storedFile.setOriginalFilename(multipartFile.getOriginalFilename());
        storedFile.setStoredFilename(storedFilename);
        storedFile.setContentType(multipartFile.getContentType());
        storedFile.setUploadTime(LocalDateTime.now());

        return storedFile;
    }

    private String getFileExtension(String filename) {
        if (Objects.nonNull(filename) && filename.contains(".")) {

            return filename.substring(filename.lastIndexOf("."));
        }

        return "";
    }
}