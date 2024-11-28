package com.strac.api.controller.drive;

import com.google.api.services.drive.model.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.strac.api.controller.drive.GoogleDriveController;
import org.strac.service.drive.GoogleDriveService;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = GoogleDriveController.class)
public class GoogleDriveControllerIntegrationTest {

    @Autowired
    private GoogleDriveController googleDriveController;

    @MockBean
    private GoogleDriveService googleDriveService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleDriveController).build();
    }

    @Test
    void testListFiles() throws Exception {
        // Arrange
        String accessToken = "mockAccessToken";
        String parentId = "mockParentId";
        File mockFile1 = new File();
        mockFile1.setId("1");
        mockFile1.setName("file1.txt");

        File mockFile2 = new File();
        mockFile2.setId("2");
        mockFile2.setName("file2.txt");

        List<File> mockFiles = Arrays.asList(mockFile1, mockFile2);

        // Mock the behavior of GoogleDriveService
        when(googleDriveService.listFiles(accessToken, parentId)).thenReturn(mockFiles);

        // Act & Assert
        mockMvc.perform(get("/api/drive/files")
                        .param("accessToken", accessToken)
                        .param("parentId", parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("file1.txt"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("file2.txt"));
    }

    @Test
    void testUploadFile() throws Exception {
        // Arrange
        String accessToken = "mockAccessToken";
        String fileName = "testFile.txt";
        byte[] fileContent = "This is a test file content.".getBytes();

        // Create a mock file to simulate the uploaded file
        MockMultipartFile mockFile = new MockMultipartFile("file", fileName, "text/plain", fileContent);

        // Act & Assert
        mockMvc.perform(multipart("/api/drive/upload")  // Use multipart() instead of post()
                        .file(mockFile)  // Simulate the file upload
                        .param("accessToken", accessToken))  // Add the access token as a parameter
                .andExpect(status().isOk());
    }


    @Test
    void testDownloadFile() throws Exception {
        // Arrange
        String fileId = "mockFileId";
        String accessToken = "mockAccessToken";
        byte[] mockFileContent = "File content".getBytes();

        // Mocking the behavior of googleDriveService.downloadFileToStream
        doAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(2);  // Get the ByteArrayOutputStream passed to the method
            outputStream.write(mockFileContent);  // Simulate writing the mock file content to the output stream
            return null;  // since the method is void
        }).when(googleDriveService).downloadFileToStream(eq(accessToken), eq(fileId), any(ByteArrayOutputStream.class));

        // Act & Assert
        mockMvc.perform(get("/api/drive/download/file")
                        .param("fileId", fileId)
                        .param("accessToken", accessToken))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockFileContent))  // Verify that the content of the response matches the mock file content
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileId + "\""));
    }

    @Test
    void testDownloadFolder() throws Exception {
        // Arrange
        String folderId = "mockFolderId";
        String accessToken = "mockAccessToken";
        byte[] mockZipContent = "Zipped folder content".getBytes();  // Simulated zipped content

        // Mocking the behavior of googleDriveService.downloadFolderAsStream
        doAnswer(invocation -> {
            ByteArrayOutputStream outputStream = invocation.getArgument(2);  // Get the ByteArrayOutputStream passed to the method
            outputStream.write(mockZipContent);  // Simulate writing the mock zip content to the output stream
            return null;  // Since the method is void
        }).when(googleDriveService).downloadFolderAsStream(eq(accessToken), eq(folderId), any(ByteArrayOutputStream.class));

        // Act & Assert
        mockMvc.perform(get("/api/drive/download/folder")
                        .param("folderId", folderId)
                        .param("accessToken", accessToken))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockZipContent))  // Verify that the content of the response matches the mock zip content
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"folder.zip\""))  // Verify that the Content-Disposition header is correct
                .andExpect(header().string("Content-Type", "application/zip"));  // Verify that the Content-Type header is correct for zip files
    }

    @Test
    void testDeleteFile() throws Exception {
        // Arrange
        String fileId = "mockFileId";
        String accessToken = "mockAccessToken";

        // Act & Assert
        mockMvc.perform(delete("/api/drive/delete")
                        .param("fileId", fileId)
                        .param("accessToken", accessToken))
                .andExpect(status().isOk());
    }
}
