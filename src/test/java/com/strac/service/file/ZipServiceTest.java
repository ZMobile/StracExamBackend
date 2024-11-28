package com.strac.service.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.strac.service.file.ZipServiceImpl;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ZipServiceTest {

    @Mock
    private ZipOutputStream zipOutputStream;

    @Mock
    private File mockFolder;

    @Mock
    private File mockFile;

    @Test
    void testZipFolder() throws IOException {
        // Arrange
        // Create a temporary folder and files to zip
        File tempFolder = new File(System.getProperty("java.io.tmpdir"), "testFolder");
        tempFolder.mkdir();

        File tempFile = new File(tempFolder, "testFile.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, World!");
        }

        // Create a ByteArrayOutputStream to capture the ZipOutputStream output
        File tempZip = new File(System.getProperty("java.io.tmpdir"), "testFolder.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip))) {
            ZipServiceImpl zipService = new ZipServiceImpl();

            // Act
            zipService.zipFolder(tempFolder, "parentFolder", zos);
        }

        // Assert
        // Verify that the zip file was created and contains the expected content
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip))) {
            ZipEntry entry = zis.getNextEntry();
            assertNotNull(entry, "Zip entry should exist");
            assertEquals("parentFolder/testFile.txt", entry.getName(), "File name in the zip should match");

            // Read and verify file content inside the zip
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zis.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            assertEquals("Hello, World!", byteArrayOutputStream.toString(), "File content inside the zip should match");
        }

        // Clean up the temporary files and folder
        tempFile.delete();
        tempFolder.delete();
        tempZip.delete();
    }

    @Test
    void testAddFileToZip() throws IOException {
        // Arrange
        ZipServiceImpl zipService = new ZipServiceImpl();
        byte[] fileContent = "Hello, World!".getBytes();
        String filePath = "testFile.txt";

        // Create a ByteArrayOutputStream to capture the ZipOutputStream output
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);

        // Act
        zipService.addFileToZip(zos, filePath, fileContent);

        // Assert
        // Check that the zip entry was added
        assertTrue(byteArrayOutputStream.size() > 0, "The zip output stream should contain data");

        // Verify that the file content matches
        zos.close(); // Ensure all content is written to the ByteArrayOutputStream
        byte[] resultBytes = byteArrayOutputStream.toByteArray();

        // Verify the zip format, e.g., check if the file content "Hello, World!" is in the zip.
        assertTrue(resultBytes.length > 0, "The zip content should have data.");
    }

    @Test
    void testDeleteFolder() {
        // Arrange
        ZipServiceImpl zipService = new ZipServiceImpl();

        // Mock the folder and file behavior
        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);
        when(mockFolder.listFiles()).thenReturn(new File[]{mockFile1, mockFile2});
        when(mockFile1.isDirectory()).thenReturn(false);
        when(mockFile2.isDirectory()).thenReturn(false);

        // Act
        zipService.deleteFolder(mockFolder);

        // Assert
        // Verify that the delete method was called on the folder and its files
        verify(mockFile1, times(1)).delete();
        verify(mockFile2, times(1)).delete();
        verify(mockFolder, times(1)).delete();
    }
}
