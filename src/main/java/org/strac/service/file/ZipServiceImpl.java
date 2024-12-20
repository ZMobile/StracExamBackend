package org.strac.service.file;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                // Recursively add folders
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                // Add file to the zip
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
            }
        }
    }

    @Override
    public void deleteFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }

    @Override
    public void addFileToZip(ZipOutputStream zos, String filePath, byte[] fileContent) throws IOException {
        // Create a new zip entry for the file
        ZipEntry zipEntry = new ZipEntry(filePath);
        zos.putNextEntry(zipEntry);
        // Write file content to the zip
        zos.write(fileContent);
        zos.closeEntry();
    }
}
