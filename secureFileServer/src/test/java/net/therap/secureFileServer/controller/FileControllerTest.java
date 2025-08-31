package net.therap.secureFileServer.controller;

import net.therap.secureFileServer.advice.GlobalExceptionHandler;
import net.therap.secureFileServer.dto.StoredFileDto;
import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.mapper.StoredFileMapper;
import net.therap.secureFileServer.service.FileStorageService;
import net.therap.secureFileServer.util.MessageUtil;
import net.therap.secureFileServer.validator.FileValidator;
import net.therap.signaturegenerator.utils.GenerateSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author avidewan
 * @since 8/31/25
 */
@WebMvcTest
@ContextConfiguration(classes = {FileController.class,
        GlobalExceptionHandler.class,
        MessageUtil.class})
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private StoredFileMapper fileMapper;

    @MockitoBean
    private FileValidator fileValidator;

    private StoredFile storedFile;
    private StoredFileDto storedFileDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        storedFile = new StoredFile();
        storedFile.setId(1L);
        storedFile.setFormId("form-123");
        storedFile.setOriginalFilename("test.txt");
        storedFile.setStoredFilename("stored-test.txt");
        storedFile.setContentType("text/plain");
        storedFile.setFileSecret("valid-signature");
        storedFile.setUploadTime(now);
        storedFile.setUploaderEmail("user@example.com");

        storedFileDto = StoredFileDto.builder()
                .formId("form-123")
                .originalFilename("test.txt")
                .contentType("text/plain")
                .downloadUrl("/files/download?formId=form-123&signature=fake-signature")
                .uploadTime(now)
                .uploaderEmail("user@example.com")
                .build();
    }

    /** --------------------- POST /files (UPLOAD) --------------------- */
    @Test
    void testUploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        Map<String, String> contextMap = Map.of("uploaderEmail", "user@example.com");

        doNothing().when(fileValidator).validate(any());

        when(fileStorageService.saveFile(any(), anyString())).thenReturn(storedFile);

        when(fileMapper.toDto(storedFile)).thenReturn(storedFileDto);

        mockMvc.perform(multipart("/files")
                        .file(file)
                        .param("uploaderEmail", "user@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formId").value("form-123"))
                .andExpect(jsonPath("$.originalFilename").value("test.txt"))
                .andExpect(jsonPath("$.downloadUrl").exists());

        verify(fileValidator, times(1)).validate(any());
        verify(fileStorageService, times(1)).saveFile(any(), eq("user@example.com"));
    }

    @Test
    void testUploadFile_missingUploaderEmail_shouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        mockMvc.perform(multipart("/files")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    /** --------------------- GET /files/download --------------------- */
    @Test
    void testDownloadFile_success() throws Exception {

        when(fileStorageService.getByFormId("form-123")).thenReturn(storedFile);

        byte[] fileContent = "Hello World".getBytes();
        Resource resource = new ByteArrayResource(fileContent);

        when(fileStorageService.loadFileAsResource(1L)).thenReturn(resource);

        try (MockedStatic<GenerateSignature> utilities = mockStatic(GenerateSignature.class)) {
            utilities.when(() -> GenerateSignature.verifySignature(storedFile.getFileSecret(), "valid-signature"))
                    .thenReturn(true);

            mockMvc.perform(get("/files/download")
                            .param("formId", "form-123")
                            .param("signature", "valid-signature"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename*=UTF-8''test.txt"))
                    .andExpect(header().string("Content-Type", "text/plain"));
        }

        verify(fileStorageService, times(1)).getByFormId("form-123");
        verify(fileStorageService, times(1)).loadFileAsResource(1L);
    }


    @Test
    void testDownloadFile_invalidSignature_shouldThrowException() throws Exception {
        when(fileStorageService.getByFormId("form-123")).thenReturn(storedFile);

        mockMvc.perform(get("/files/download")
                        .param("formId", "form-123")
                        .param("signature", "wrong-signature"))
                .andExpect(status().isForbidden());

        verify(fileStorageService, times(1)).getByFormId("form-123");
    }

    /** --------------------- GET /files (LIST) --------------------- */
    @Test
    void testListFiles_success() throws Exception {
        when(fileStorageService.getAllFiles()).thenReturn(List.of(storedFile));
        when(fileMapper.toDto(any())).thenReturn(storedFileDto);

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formId").value("form-123"))
                .andExpect(jsonPath("$[0].originalFilename").value("test.txt"));
    }

    /** --------------------- DELETE /files/hard/{formId} --------------------- */
    @Test
    void testDeleteFile_success() throws Exception {
        doNothing().when(fileStorageService).deleteByFormId("form-123");

        mockMvc.perform(delete("/files/hard/form-123"))
                .andExpect(status().isNoContent());

        verify(fileStorageService, times(1)).deleteByFormId("form-123");
    }
}