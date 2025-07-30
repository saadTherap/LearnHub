package net.therap.secureFileServer.validator;

import net.therap.secureFileServer.config.StorageProperties;
import net.therap.secureFileServer.exception.EmptyFileException;
import net.therap.secureFileServer.exception.FileSizeExceededException;
import net.therap.secureFileServer.exception.MaliciousFileDetectedException;
import net.therap.secureFileServer.exception.UnsupportedFileTypeException;
import net.therap.secureFileServer.service.MalwareScanService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author avidewan
 * @since 7/23/25
 */
@Component
public class FileValidator {

    private final StorageProperties storageProperties;
    private final MalwareScanService malwareScanService;

    public FileValidator(StorageProperties storageProperties, MalwareScanService malwareScanService) {
        this.storageProperties = storageProperties;
        this.malwareScanService = malwareScanService;
    }

    public void validate(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File must not be empty");
        }

        if (file.getSize() > storageProperties.getMaxFileSize()) {
            throw new FileSizeExceededException("File size exceeds the maximum allowed limit.");
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = storageProperties.getAllowedFileTypes();

        if (Objects.isNull(contentType) || !allowedTypes.contains(contentType)) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + contentType);
        }

        if (storageProperties.isEnableVirusScan() && !malwareScanService.scan(file.getInputStream())) {
            throw new MaliciousFileDetectedException("File is infected with a virus!");
        }
    }
}