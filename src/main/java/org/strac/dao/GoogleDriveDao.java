package org.strac.dao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface GoogleDriveDao {
    /**
     * List files from the user's Google Drive.
     * If a parent ID is provided, lists files under that folder.
     * If no parent ID is provided, lists top-level files.
     *
     * @param credential The OAuth credential containing the access token.
     * @param parentId   The parent folder ID (optional).
     * @return A list of files from Google Drive.
     */
    FileList listFiles(Credential credential, String parentId);

    /**
     * Upload a file to Google Drive.
     *
     * @param credential The OAuth credential containing the access token.
     * @param localFile  The local file to upload.
     * @param mimeType   The MIME type of the file.
     * @param folderId   The ID of the folder where the file should be uploaded (optional).
     * @return The uploaded file's metadata.
     */
    File uploadFile(Credential credential, java.io.File localFile, String mimeType, String folderId);

    void downloadFileToStream(Credential credential, String fileId, OutputStream outputStream);

    /**
     * Delete a file from Google Drive.
     *
     * @param credential The OAuth credential containing the access token.
     * @param fileId     The ID of the file to delete.
     */
    void deleteFile(Credential credential, String fileId);
}
