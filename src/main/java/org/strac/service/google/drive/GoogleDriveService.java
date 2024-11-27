package org.strac.service.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

public interface GoogleDriveService {
    /**
     * Upload a file to Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param multipartFile The file to upload.
     * @param folderId The target folder ID (optional).
     * @return Metadata of the uploaded file.
     */
    File uploadFile(String accessToken, MultipartFile multipartFile, String folderId);

    /**
     * List files from Google Drive.
     * If a parent folder ID is provided, lists files under that folder.
     * Otherwise, lists top-level files.
     *
     * @param accessToken The access token for Google API.
     * @param parentId The parent folder ID (optional).
     * @return List of files.
     */
    List<File> listFiles(String accessToken, String parentId);

    void downloadFileToStream(String accessToken, String fileId, OutputStream outputStream);

    void downloadFolderAsStream(String accessToken, String folderId, OutputStream outputStream);

    /**
     * Delete a file from Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param fileId The ID of the file to delete.
     */
    void deleteFile(String accessToken, String fileId);
}
