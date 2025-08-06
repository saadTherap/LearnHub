package net.therap.secureFileServer.validator;

import lombok.RequiredArgsConstructor;
import net.therap.secureFileServer.config.StorageProperties;
import net.therap.secureFileServer.exception.EmptyFileException;
import net.therap.secureFileServer.exception.FileSizeExceededException;
import net.therap.secureFileServer.exception.MaliciousFileDetectedException;
import net.therap.secureFileServer.exception.UnsupportedFileTypeException;
import net.therap.secureFileServer.service.MalwareScanService;
import net.therap.secureFileServer.util.MessageUtil;
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
@RequiredArgsConstructor
public class FileValidator {

    private final StorageProperties storageProperties;
    private final MalwareScanService malwareScanService;
    private final MessageUtil messageUtil;

    public void validate(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException(messageUtil.getMessage("error.empty-file.message"));
        }

        if (file.getSize() > storageProperties.getMaxFileSize()) {
            throw new FileSizeExceededException(messageUtil.getMessage("error.file-too-large.message"));
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = storageProperties.getAllowedFileTypes();

        if (Objects.isNull(contentType) || !allowedTypes.contains(contentType)) {
            throw new UnsupportedFileTypeException(messageUtil.getMessage("error.unsupported-file-type.message"));
        }

        if (storageProperties.isEnableVirusScan() && !malwareScanService.scan(file.getInputStream())) {
            throw new MaliciousFileDetectedException(messageUtil.getMessage("error.malicious-file.message"));
        }
    }
}