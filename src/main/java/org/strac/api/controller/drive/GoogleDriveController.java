package org.strac.api.controller.drive;

import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.strac.service.google.drive.GoogleDriveService;

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
     * Download a file from Google Drive.
     *
     * @param fileId         The ID of the file to download.
     * @param destinationPath The local path where the file should be saved.
     * @param accessToken    The access token extracted from the SecurityContext.
     */
    @GetMapping("/download/file")
    public void downloadFile(@RequestParam("fileId") String fileId,
                             @RequestParam("destinationPath") String destinationPath,
                             @AuthenticationPrincipal String accessToken) {
        googleDriveService.downloadFile(accessToken, fileId, destinationPath);
    }

    /**
     * Download a folder from Google Drive.
     *
     * @param folderId         The ID of the folder to download.
     * @param destinationPath The local path where the folder should be saved.
     * @param accessToken    The access token extracted from the SecurityContext.
     */
    @GetMapping("/download/folder")
    public void downloadFolder(@RequestParam("folderId") String folderId,
                               @RequestParam("destinationPath") String destinationPath,
                               @AuthenticationPrincipal String accessToken) {
        googleDriveService.downloadFolder(accessToken, folderId, destinationPath);
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
        googleDriveService.deleteFile(accessToken, fileId);
    }
}
