package org.strac.service.drive;

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
    void uploadFile(String accessToken, MultipartFile multipartFile, String folderId);

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

    /**
     * Download a file from Google Drive to a stream.
     *
     * @param accessToken The access token for Google API.
     * @param fileId The ID of the file to download.
     * @param outputStream The OutputStream to write the file to.
     */
    void downloadFileToStream(String accessToken, String fileId, OutputStream outputStream);

    /**
     * Download a folder from Google Drive as a zip.
     *
     * @param accessToken The access token for Google API.
     * @param folderId The ID of the folder to download.
     * @param outputStream The OutputStream to write the zip to.
     */
    void downloadFolderAsStream(String accessToken, String folderId, OutputStream outputStream);

    /**
     * Delete a file from Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param fileId The ID of the file to delete.
     */
    void deleteFile(String accessToken, String fileId);
}
