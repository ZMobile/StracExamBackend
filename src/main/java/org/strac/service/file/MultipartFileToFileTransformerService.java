package org.strac.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface MultipartFileToFileTransformerService {
    /**
     * Convert a MultipartFile to a java.io.File.
     *
     * @param file The MultipartFile to convert.
     * @return The converted java.io.File.
     * @throws IOException If an error occurs during file conversion.
     */
    File convertMultipartFileToFile(MultipartFile file) throws IOException;
}
