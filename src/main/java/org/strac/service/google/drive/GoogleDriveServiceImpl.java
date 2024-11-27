package org.strac.service.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.strac.dao.GoogleDriveDao;
import org.strac.service.file.MultipartFileToFileTransformerService;

import java.util.List;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {
    private final GoogleDriveDao googleDriveDao;
    private final GoogleDriveCredentialService googleDriveCredentialService;
    private final MultipartFileToFileTransformerService multipartFileToFileTransformerService;

    public GoogleDriveServiceImpl(GoogleDriveDao googleDriveDao,
                                  GoogleDriveCredentialService googleDriveCredentialService,
                                  MultipartFileToFileTransformerService multipartFileToFileTransformerService) {
        this.googleDriveDao = googleDriveDao;
        this.googleDriveCredentialService = googleDriveCredentialService;
        this.multipartFileToFileTransformerService = multipartFileToFileTransformerService;
    }

    /**
     * Upload a file to Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param multipartFile The file to upload.
     * @param folderId The target folder ID (optional).
     * @return Metadata of the uploaded file.
     */
    public File uploadFile(String accessToken, MultipartFile multipartFile, String folderId) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            // Convert MultipartFile to java.io.File
            java.io.File file = multipartFileToFileTransformerService.convertMultipartFileToFile(multipartFile);

            return googleDriveDao.uploadFile(credential, file, multipartFile.getContentType(), folderId);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Google Drive", e);
        }
    }

    /**
     * List files from Google Drive.
     * If a parent folder ID is provided, lists files under that folder.
     * Otherwise, lists top-level files.
     *
     * @param accessToken The access token for Google API.
     * @param parentId The parent folder ID (optional).
     * @return List of files.
     */
    public List<File> listFiles(String accessToken, String parentId) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            // Fetch file list using DAO
            FileList fileList = googleDriveDao.listFiles(credential, parentId);

            return fileList.getFiles();
        } catch (Exception e) {
            throw new RuntimeException("Error listing files from Google Drive", e);
        }
    }

    /**
     * Download a file from Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param fileId The ID of the file to download.
     * @param destinationPath The local destination path for the downloaded file.
     */
    public void downloadFile(String accessToken, String fileId, String destinationPath) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            googleDriveDao.downloadFile(credential, fileId, destinationPath);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from Google Drive", e);
        }
    }

    /**
     * Delete a file from Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param fileId The ID of the file to delete.
     */
    public void deleteFile(String accessToken, String fileId) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            googleDriveDao.deleteFile(credential, fileId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from Google Drive", e);
        }
    }
}
