package net.therap.secureFileServer.controller;

import net.therap.secureFileServer.entity.StoredFile;
import net.therap.secureFileServer.mapper.StoredFileMapper;
import net.therap.secureFileServer.service.FileStorageService;
import net.therap.secureFileServer.validator.FileValidator;
import net.therap.signaturegenerator.utils.GenerateSignature;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author avidewan
 * @since 8/31/25
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private StoredFileMapper fileMapper;

    @Autowired
    private FileValidator fileValidator;

    /** --------------------- UPLOAD --------------------- */
    @Test
    void testUploadFile_fullIntegration() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        mockMvc.perform(multipart("/files")
                        .file(file)
                        .param("uploaderEmail", "user@example.com"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formId").exists())
                .andExpect(jsonPath("$.downloadUrl").exists());
    }

    /** --------------------- DOWNLOAD --------------------- */
    @Test
    void testDownloadFile_fullIntegration() throws Exception {
        StoredFile savedFile = fileStorageService.saveFile(
                new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes()),
                "user@example.com"
        );

        try (MockedStatic<GenerateSignature> utilities = mockStatic(GenerateSignature.class)) {
            utilities.when(() -> GenerateSignature.verifySignature(savedFile.getFileSecret(), "valid-signature"))
                    .thenReturn(true);

            mockMvc.perform(get("/files/download")
                            .param("formId", savedFile.getFormId())
                            .param("signature", "valid-signature"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename*=UTF-8''test.txt"))
                    .andExpect(header().string("Content-Type", "text/plain"));
        }
    }

    /** --------------------- DELETE --------------------- */
    @Test
    void testDeleteFile_fullIntegration() throws Exception {
        StoredFile savedFile = fileStorageService.saveFile(
                new MockMultipartFile("file", "delete.txt", "text/plain", "Delete Me".getBytes()),
                "user@example.com"
        );

        mockMvc.perform(delete("/files/hard/" + savedFile.getFormId()))
                .andExpect(status().isNoContent());
    }
}

