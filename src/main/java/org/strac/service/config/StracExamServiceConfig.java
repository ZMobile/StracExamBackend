package org.strac.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.strac.dao.GoogleDriveDao;
import org.strac.dao.config.StracExamDaoConfig;
import org.strac.service.file.ZipService;
import org.strac.service.file.ZipServiceImpl;
import org.strac.service.google.drive.GoogleDriveCredentialService;
import org.strac.service.google.drive.GoogleDriveCredentialServiceImpl;
import org.strac.service.google.drive.GoogleDriveService;
import org.strac.service.google.drive.GoogleDriveServiceImpl;
import org.strac.service.file.MultiPartFileToFileTransformerServiceImpl;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.service.google.token.GoogleAccessTokenValidatorService;
import org.strac.service.google.token.GoogleAccessTokenValidatorServiceImpl;

@Configuration
@Import({StracExamDaoConfig.class})
public class StracExamServiceConfig {
    @Bean
    public GoogleDriveService googleDriveService(GoogleDriveDao googleDriveDao,
                                                 GoogleDriveCredentialService googleDriveCredentialService,
                                                 MultipartFileToFileTransformerService multipartFileToFileTransformerService,
                                                 ZipService zipService) {
        return new GoogleDriveServiceImpl(googleDriveDao, googleDriveCredentialService, multipartFileToFileTransformerService, zipService);
    }

    @Bean
    public GoogleDriveCredentialService googleDriveCredentialService() {
        return new GoogleDriveCredentialServiceImpl();
    }

    @Bean
    public MultipartFileToFileTransformerService multipartFileToFileTransformerService() {
        return new MultiPartFileToFileTransformerServiceImpl();
    }

    @Bean
    public ZipService zipService() {
        return new ZipServiceImpl();
    }

    @Bean
    public GoogleAccessTokenValidatorService tokenValidatorService() {
        return new GoogleAccessTokenValidatorServiceImpl();
    }
}
