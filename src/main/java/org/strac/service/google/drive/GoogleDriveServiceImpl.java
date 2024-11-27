package org.strac.service.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.strac.dao.GoogleDriveDao;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.service.file.ZipService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {
    private final GoogleDriveDao googleDriveDao;
    private final GoogleDriveCredentialService googleDriveCredentialService;
    private final MultipartFileToFileTransformerService multipartFileToFileTransformerService;
    private final ZipService zipService;

    public GoogleDriveServiceImpl(GoogleDriveDao googleDriveDao,
                                  GoogleDriveCredentialService googleDriveCredentialService,
                                  MultipartFileToFileTransformerService multipartFileToFileTransformerService,
                                  ZipService zipService) {
        this.googleDriveDao = googleDriveDao;
        this.googleDriveCredentialService = googleDriveCredentialService;
        this.multipartFileToFileTransformerService = multipartFileToFileTransformerService;
        this.zipService = zipService;
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
     * Download a folder from Google Drive.
     *
     * @param accessToken The access token for Google API.
     * @param folderId The ID of the folder to download.
     * @param destinationPath The local destination path for the downloaded folder.
     */
    public void downloadFolder(String accessToken, String folderId, String destinationPath) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            // Create a temporary folder for downloaded contents
            java.io.File tempFolder = new java.io.File("temp_download");
            if (!tempFolder.exists()) {
                tempFolder.mkdir();
            }

            // Recursively fetch and download folder contents
            fetchAndDownloadFolderContents(credential, folderId, tempFolder);

            // Zip the folder using the ZipService
            java.io.File zipFile = new java.io.File(destinationPath);
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                zipService.zipFolder(tempFolder, tempFolder.getName(), zos);
            }

            // Cleanup temporary files using the ZipService
            zipService.deleteFolder(tempFolder);

        } catch (Exception e) {
            throw new RuntimeException("Error downloading folder from Google Drive", e);
        }
    }

    private void fetchAndDownloadFolderContents(Credential credential, String folderId, java.io.File localFolder) {
        FileList fileList = googleDriveDao.listFiles(credential, folderId);
        for (File file : fileList.getFiles()) {
            if ("application/vnd.google-apps.folder".equals(file.getMimeType())) {
                // If the file is a folder, recursively fetch its contents
                java.io.File subFolder = new java.io.File(localFolder, file.getName());
                subFolder.mkdir();
                fetchAndDownloadFolderContents(credential, file.getId(), subFolder);
            } else {
                // If it's a file, download it
                java.io.File localFile = new java.io.File(localFolder, file.getName());
                googleDriveDao.downloadFile(credential, file.getId(), localFile.getAbsolutePath());
            }
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
