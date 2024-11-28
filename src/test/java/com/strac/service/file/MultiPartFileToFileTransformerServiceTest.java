package com.strac.service.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.strac.service.file.MultiPartFileToFileTransformerServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultiPartFileToFileTransformerServiceTest {

    @Mock
    private MultipartFile multipartFile;

    @Test
    void testConvertMultipartFileToFile() throws IOException {
        // Arrange
        String fileName = "testFile.txt";
        String tempDir = System.getProperty("java.io.tmpdir");
        String expectedFilePath = Paths.get(tempDir, fileName).toString();  // Safely join paths using Paths.get

        // Mock the MultipartFile
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        // Mock transferTo method to avoid actual file creation
        doNothing().when(multipartFile).transferTo(any(File.class));

        // Create the service instance
        MultiPartFileToFileTransformerServiceImpl service = new MultiPartFileToFileTransformerServiceImpl();

        // Act
        File result = service.convertMultipartFileToFile(multipartFile);

        // Assert
        // Verify that the file is created in the correct location
        assertNotNull(result, "The result file should not be null");
        assertEquals(expectedFilePath, result.getAbsolutePath(), "The file path should match the expected path");

        // Verify that the transferTo method was called on the multipartFile
        verify(multipartFile, times(1)).transferTo(result);
    }
}
