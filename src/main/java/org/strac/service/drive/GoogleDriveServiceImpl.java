package org.strac.service.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.strac.dao.GoogleDriveDao;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.service.file.ZipService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public void uploadFile(String accessToken, MultipartFile multipartFile, String folderId) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            // Convert MultipartFile to java.io.File
            java.io.File file = multipartFileToFileTransformerService.convertMultipartFileToFile(multipartFile);

            googleDriveDao.uploadFile(credential, file, multipartFile.getContentType(), folderId);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Google Drive", e);
        }
    }

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

    @Override
    public void downloadFileToStream(String accessToken, String fileId, OutputStream outputStream) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);
            googleDriveDao.downloadFileToStream(credential, fileId, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from Google Drive", e);
        }
    }


    /**
     * Download a folder from Google Drive as a zipped stream.
     *
     * @param accessToken The access token for Google API.
     * @param folderId    The ID of the folder to download.
     * @param outputStream The HTTP output stream to write the zip data to.
     */
    @Override
    public void downloadFolderAsStream(String accessToken, String folderId, OutputStream outputStream) {
        try {
            Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

            // Use a ZipOutputStream to stream the folder contents
            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                fetchAndDownloadFolderContentsToStream(credential, folderId, "", zos);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error streaming folder from Google Drive", e);
        }
    }

    private void fetchAndDownloadFolderContentsToStream(Credential credential, String folderId, String currentPath, ZipOutputStream zos) {
        FileList fileList = googleDriveDao.listFiles(credential, folderId);
        for (File file : fileList.getFiles()) {
            String filePath = currentPath.isEmpty() ? file.getName() : currentPath + "/" + file.getName();
            if ("application/vnd.google-apps.folder".equals(file.getMimeType())) {
                // Recursively fetch and download folder contents
                fetchAndDownloadFolderContentsToStream(credential, file.getId(), filePath, zos);
            } else {
                // If it's a file, add it to the zip
                try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
                    googleDriveDao.downloadFileToStream(credential, file.getId(), fileOutputStream);
                    zipService.addFileToZip(zos, filePath, fileOutputStream.toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException("Error adding file to zip: " + filePath, e);
                }
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
