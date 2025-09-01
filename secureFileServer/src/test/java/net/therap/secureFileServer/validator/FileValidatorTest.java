package net.therap.secureFileServer.validator;

import net.therap.secureFileServer.config.StorageProperties;
import net.therap.secureFileServer.exception.*;
import net.therap.secureFileServer.service.MalwareScanService;
import net.therap.secureFileServer.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author avidewan
 * @since 8/31/25
 */
class FileValidatorTest {

    private StorageProperties storageProperties;
    private MalwareScanService malwareScanService;
    private MessageUtil messageUtil;
    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        storageProperties = mock(StorageProperties.class);
        malwareScanService = mock(MalwareScanService.class);
        messageUtil = mock(MessageUtil.class);

        fileValidator = new FileValidator(storageProperties, malwareScanService, messageUtil);
    }

    @Test
    void shouldThrowEmptyFileExceptionIfFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        when(messageUtil.getMessage("error.empty-file.message")).thenReturn("File is empty");

        EmptyFileException exception = assertThrows(EmptyFileException.class,
                () -> fileValidator.validate(file));

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void shouldThrowFileSizeExceededExceptionIfFileTooLarge() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1025L);

        when(storageProperties.getMaxFileSize()).thenReturn(1024L);
        when(messageUtil.getMessage("error.file-too-large.message")).thenReturn("File too large");

        FileSizeExceededException exception = assertThrows(FileSizeExceededException.class,
                () -> fileValidator.validate(file));

        assertEquals("File too large", exception.getMessage());
    }

    @Test
    void shouldThrowUnsupportedFileTypeExceptionIfContentTypeNotAllowed() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getContentType()).thenReturn("application/xyz");

        when(storageProperties.getMaxFileSize()).thenReturn(1024L);
        when(storageProperties.getAllowedFileTypes()).thenReturn(List.of("image/png", "image/jpeg"));
        when(messageUtil.getMessage("error.unsupported-file-type.message"))
                .thenReturn("Unsupported file type");

        UnsupportedFileTypeException exception = assertThrows(UnsupportedFileTypeException.class,
                () -> fileValidator.validate(file));

        assertEquals("Unsupported file type", exception.getMessage());
    }

    @Test
    void shouldThrowMaliciousFileDetectedExceptionIfVirusScanFails() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        when(storageProperties.getMaxFileSize()).thenReturn(1024L);
        when(storageProperties.getAllowedFileTypes()).thenReturn(List.of("image/png"));
        when(storageProperties.isEnableVirusScan()).thenReturn(true);
        when(malwareScanService.scan(any())).thenReturn(false);
        when(messageUtil.getMessage("error.malicious-file.message")).thenReturn("Malicious file detected");

        MaliciousFileDetectedException exception = assertThrows(MaliciousFileDetectedException.class,
                () -> fileValidator.validate(file));

        assertEquals("Malicious file detected", exception.getMessage());
    }

    @Test
    void shouldPassValidationForValidFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        when(storageProperties.getMaxFileSize()).thenReturn(1024L);
        when(storageProperties.getAllowedFileTypes()).thenReturn(List.of("image/png"));
        when(storageProperties.isEnableVirusScan()).thenReturn(true);
        when(malwareScanService.scan(any())).thenReturn(true);

        assertDoesNotThrow(() -> fileValidator.validate(file));
    }
}