package org.strac.dao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Repository
public class GoogleDriveDaoImpl implements GoogleDriveDao {

    /**
     * List files from the user's Google Drive.
     * If a parent ID is provided, lists files under that folder.
     * If no parent ID is provided, lists top-level files.
     *
     * @param credential The OAuth credential containing the access token.
     * @param parentId   The parent folder ID (optional).
     * @return A list of files from Google Drive.
     */
    @Override
    public FileList listFiles(Credential credential, String parentId) {
        try {
            // Create a Drive service instance
            Drive driveService = createDriveService(credential);

            // Build the query for top-level files or specific folder contents
            String query = parentId == null
                    ? "'root' in parents and trashed = false"
                    : "'" + parentId + "' in parents and trashed = false";

            // Fetch the list of files
            return driveService.files().list()
                    .setQ(query)
                    //.setPageSize(10) // Limit the number of files returned
                    .setFields("nextPageToken, files(id, name, mimeType, modifiedTime)") // Specify fields to fetch
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error listing files from Google Drive", e);
        }
    }

    /**
     * Upload a file to Google Drive.
     *
     * @param credential The OAuth credential containing the access token.
     * @param localFile  The local file to upload.
     * @param mimeType   The MIME type of the file.
     * @param folderId   The ID of the folder where the file should be uploaded (optional).
     * @return The uploaded file's metadata.
     */
    @Override
    public File uploadFile(Credential credential, java.io.File localFile, String mimeType, String folderId) {
        try {
            // Create a Drive service instance
            Drive driveService = createDriveService(credential);

            // Metadata for the uploaded file
            File fileMetadata = new File();
            fileMetadata.setName(localFile.getName());

            if (folderId != null) {
                fileMetadata.setParents(java.util.Collections.singletonList(folderId));
            }

            // Create and execute the upload request
            return driveService.files().create(fileMetadata,
                            new com.google.api.client.http.FileContent(mimeType, localFile))
                    .setFields("id, name")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to Google Drive", e);
        }
    }

    /**
     * Download a file from Google Drive.
     *
     * @param credential The OAuth credential containing the access token.
     * @param fileId     The ID of the file to download.
     * @param destinationPath The local destination path for the downloaded file.
     */
    @Override
    public void downloadFile(Credential credential, String fileId, String destinationPath) {
        try {
            // Create a Drive service instance
            Drive driveService = createDriveService(credential);

            // Create output stream for the local file
            try (OutputStream outputStream = new FileOutputStream(destinationPath)) {
                driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error downloading file from Google Drive", e);
        }
    }

    /**
     * Delete a file from Google Drive.
     *
     * @param credential The OAuth credential containing the access token.
     * @param fileId     The ID of the file to delete.
     */
    @Override
    public void deleteFile(Credential credential, String fileId) {
        try {
            // Create a Drive service instance
            Drive driveService = createDriveService(credential);

            // Execute the delete request
            driveService.files().delete(fileId).execute();
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file from Google Drive", e);
        }
    }

    /**
     * Helper method to create a Drive service instance.
     *
     * @param credential The OAuth credential containing the access token.
     * @return A configured Drive service instance.
     */
    private Drive createDriveService(Credential credential) {
        return new Drive.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential)
                .setApplicationName("Strac Exam")
                .build();
    }
}
