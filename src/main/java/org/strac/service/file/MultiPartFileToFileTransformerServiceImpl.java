package org.strac.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class MultiPartFileToFileTransformerServiceImpl implements MultipartFileToFileTransformerService {
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        java.io.File convertedFile = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return convertedFile;
    }
}
