package org.strac.service.file;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface ZipService {
    void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException;

    void deleteFolder(File folder);
}

