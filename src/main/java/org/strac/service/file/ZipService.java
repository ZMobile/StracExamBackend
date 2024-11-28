package org.strac.service.file;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface ZipService {
    /**
     * Zip a folder and its contents.
     *
     * @param folder The folder to zip.
     * @param parentFolder The parent folder of the folder to zip.
     * @param zos The ZipOutputStream to write the zip to.
     * @throws IOException If an I/O error occurs.
     */
    void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException;

    /**
     * Delete a folder and its contents.
     *
     * @param folder The folder to delete.
     */
    void deleteFolder(File folder);

    /**
     * Add a file to a zip.
     *
     * @param zos The ZipOutputStream to write the file to.
     * @param filePath The path of the file in the zip.
     * @param fileContent The content of the file.
     * @throws IOException If an I/O error occurs.
     */
    void addFileToZip(ZipOutputStream zos, String filePath, byte[] fileContent) throws IOException;
}

