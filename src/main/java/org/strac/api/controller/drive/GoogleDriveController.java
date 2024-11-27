package org.strac.api.controller.drive;

import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.strac.service.google.drive.GoogleDriveService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/drive")
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;

    /**
     * List files from Google Drive.
     * Optionally specify a parent folder ID to list files in that folder.
     *
     * @param accessToken The access token extracted from the SecurityContext.
     * @param parentId    The parent folder ID (optional).
     * @return List of files.
     */
    @GetMapping("/files")
    public List<File> listFiles(@AuthenticationPrincipal String accessToken,
                                @RequestParam(value = "parentId", required = false) String parentId) {
        return googleDriveService.listFiles(accessToken, parentId);
    }

    /**
     * Upload a file to Google Drive.
     * Optionally specify a folder ID where the file should be uploaded.
     *
     * @param file        The file to upload.
     * @param folderId    The target folder ID (optional).
     * @param accessToken The access token extracted from the SecurityContext.
     * @return Metadata of the uploaded file.
     */
    @PostMapping("/upload")
    public File uploadFile(@RequestParam("file") MultipartFile file,
                           @RequestParam(value = "folderId", required = false) String folderId,
                           @AuthenticationPrincipal String accessToken) {
        return googleDriveService.uploadFile(accessToken, file, folderId);
    }

    /**
     * Download a file from Google Drive and serve it over HTTP.
     *
     * @param fileId      The ID of the file to download.
     * @param accessToken The access token extracted from the SecurityContext.
     * @return The file as a ResponseEntity with binary data.
     */
    @GetMapping("/download/file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileId") String fileId,
                                               @AuthenticationPrincipal String accessToken) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Log incoming request
            System.out.println("Received request to download file with ID: " + fileId);
            System.out.println("Access token: " + accessToken);

            // Download the file into an output stream
            googleDriveService.downloadFileToStream(accessToken, fileId, outputStream);
            System.out.println("File downloaded successfully into memory.");

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            // Log response size
            System.out.println("Returning file with size: " + outputStream.size() + " bytes");

            // Return the file as a response
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            // Log error
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();

            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading file: " + e.getMessage()).getBytes());
        }
    }


    /**
     * Download a folder from Google Drive and serve it as a zipped stream over HTTP.
     *
     * @param folderId    The ID of the folder to download.
     * @param accessToken The access token extracted from the SecurityContext.
     * @return The folder as a zipped ResponseEntity.
     */
    @GetMapping("/download/folder")
    public ResponseEntity<byte[]> downloadFolder(@RequestParam("folderId") String folderId,
                                                 @AuthenticationPrincipal String accessToken) {
        try (ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream()) {
            // Stream folder contents as a ZIP to the output stream
            googleDriveService.downloadFolderAsStream(accessToken, folderId, zipOutputStream);

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"folder.zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

            // Return the zipped folder as a response
            return new ResponseEntity<>(zipOutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading folder: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Delete a file from Google Drive.
     *
     * @param fileId      The ID of the file to delete.
     * @param accessToken The access token extracted from the SecurityContext.
     */
    @DeleteMapping("/delete")
    public void deleteFile(@RequestParam("fileId") String fileId,
                           @AuthenticationPrincipal String accessToken) {
        System.out.println("Test 00");
        googleDriveService.deleteFile(accessToken, fileId);
    }
}
