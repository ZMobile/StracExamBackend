package com.strac.service.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.strac.dao.GoogleDriveDao;
import org.strac.service.drive.GoogleDriveCredentialService;
import org.strac.service.drive.GoogleDriveServiceImpl;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.service.file.ZipService;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoogleDriveServiceTest {

    @Mock
    private GoogleDriveDao googleDriveDao;

    @Mock
    private GoogleDriveCredentialService googleDriveCredentialService;

    @Mock
    private MultipartFileToFileTransformerService multipartFileToFileTransformerService;

    @Mock
    private ZipService zipService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private GoogleDriveServiceImpl googleDriveService;

    private String accessToken = "mockAccessToken";
    private String folderId = "mockFolderId";
    private String fileId = "mockFileId";

    @BeforeEach
    void setUp() {
        googleDriveService = new GoogleDriveServiceImpl(googleDriveDao, googleDriveCredentialService,
                multipartFileToFileTransformerService, zipService);
    }

    @Test
    void testUploadFile() throws IOException {
        // Arrange
        Credential mockCredential = mock(Credential.class);
        when(googleDriveCredentialService.createCredentialFromAccessToken(accessToken)).thenReturn(mockCredential);
        java.io.File mockFile = mock(java.io.File.class);
        when(multipartFileToFileTransformerService.convertMultipartFileToFile(multipartFile)).thenReturn(mockFile);
        File mockFileMetadata = new File();
        when(googleDriveDao.uploadFile(mockCredential, mockFile, multipartFile.getContentType(), folderId))
                .thenReturn(mockFileMetadata);

        googleDriveService.uploadFile(accessToken, multipartFile, folderId);

        verify(googleDriveDao).uploadFile(mockCredential, mockFile, multipartFile.getContentType(), folderId);
    }

    @Test
    void testListFiles() {
        // Arrange
        Credential mockCredential = mock(Credential.class);
        when(googleDriveCredentialService.createCredentialFromAccessToken(accessToken)).thenReturn(mockCredential);
        File mockFile1 = new File();
        File mockFile2 = new File();
        FileList mockFileList = new FileList();
        mockFileList.setFiles(Arrays.asList(mockFile1, mockFile2));
        when(googleDriveDao.listFiles(mockCredential, folderId)).thenReturn(mockFileList);

        // Act
        List<File> result = googleDriveService.listFiles(accessToken, folderId);

        // Assert
        assertEquals(2, result.size());
        verify(googleDriveDao).listFiles(mockCredential, folderId);
    }

    @Test
    void testDownloadFileToStream() {
        // Arrange
        Credential mockCredential = mock(Credential.class);
        when(googleDriveCredentialService.createCredentialFromAccessToken(accessToken)).thenReturn(mockCredential);
        OutputStream outputStream = new ByteArrayOutputStream();

        // Act
        googleDriveService.downloadFileToStream(accessToken, fileId, outputStream);

        // Assert
        verify(googleDriveDao).downloadFileToStream(mockCredential, fileId, outputStream);
    }

    @Test
    void testDownloadFolderAsStream() {
        // Arrange
        String accessToken = "mockAccessToken";
        String folderId = "mockFolderId";

        // Mock the Credential creation from the accessToken
        Credential mockCredential = mock(Credential.class);
        when(googleDriveCredentialService.createCredentialFromAccessToken(accessToken)).thenReturn(mockCredential);

        // Mock OutputStream to capture the data written
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Mock a folder and a file in the folder
        File mockFolder = new File();
        mockFolder.setId("mockFolderId");
        mockFolder.setMimeType("application/vnd.google-apps.folder");
        mockFolder.setName("mockFolder");

        File mockFile = new File();
        mockFile.setId("mockFileId");
        mockFile.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");  // Example MIME type
        mockFile.setName("mockFile.xlsx");

        // Mock the FileList to return both the folder and the file
        FileList mockFileList = mock(FileList.class);
        //when(mockFileList.getFiles()).thenReturn(Arrays.asList(mockFolder, mockFile));

        // Mock the googleDriveDao to return the mocked FileList
        when(googleDriveDao.listFiles(mockCredential, folderId)).thenReturn(mockFileList);

        // Mock the behavior when encountering a folder:
        // Return an empty list of files when listing files for a folder (stopping the recursion)
        FileList mockEmptyFileList = mock(FileList.class);
        when(mockEmptyFileList.getFiles()).thenReturn(Collections.emptyList());
        when(googleDriveDao.listFiles(mockCredential, mockFolder.getId())).thenReturn(mockEmptyFileList);

        /* Mock the googleDriveDao.downloadFileToStream for the file (not the folder)
        ByteArrayOutputStream mockFileOutputStream = new ByteArrayOutputStream();
        /*when(googleDriveDao.downloadFileToStream(mockCredential, mockFile.getId(), mockFileOutputStream))
                .thenAnswer(invocation -> {
                    mockFileOutputStream.write("File Content".getBytes());  // Simulate file content writing
                    return null;
                });*/

        // Mock the zipService.addFileToZip to avoid actual zipping
        //doNothing().when(zipService).addFileToZip(any(ZipOutputStream.class), anyString(), any(byte[].class));

        // Act
        googleDriveService.downloadFolderAsStream(accessToken, folderId, outputStream);

        // Assert
        // Verify that listFiles was called with correct arguments
        verify(googleDriveDao).listFiles(mockCredential, folderId);
        verify(googleDriveDao).listFiles(mockCredential, mockFolder.getId());  // Ensure recursion is stopped

        // Verify that addFileToZip was called for the file in the folder
        //verify(zipService, times(1)).addFileToZip(any(ZipOutputStream.class), eq("mockFolder/mockFile.xlsx"), any(byte[].class));

        // You can also check the output stream for what was written, if needed
        assertNotNull(outputStream);  // Check if the output stream is not null (indicating the method executed)

        // Optionally check if the data has been written to the output stream
        assertTrue(outputStream.size() > 0, "Output stream should contain data");
    }

    @Test
    void testDeleteFile() throws IOException {
        // Arrange
        Credential mockCredential = mock(Credential.class);
        when(googleDriveCredentialService.createCredentialFromAccessToken(accessToken)).thenReturn(mockCredential);

        // Act
        googleDriveService.deleteFile(accessToken, fileId);

        // Assert
        verify(googleDriveDao).deleteFile(mockCredential, fileId);
    }
}
