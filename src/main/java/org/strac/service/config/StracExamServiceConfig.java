package org.strac.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.strac.dao.GoogleDriveDao;
import org.strac.dao.config.StracExamDaoConfig;
import org.strac.service.google.drive.GoogleDriveCredentialService;
import org.strac.service.google.drive.GoogleDriveCredentialServiceImpl;
import org.strac.service.google.drive.GoogleDriveService;
import org.strac.service.google.drive.GoogleDriveServiceImpl;
import org.strac.service.file.MultiPartFileToFileTransformerServiceImpl;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.service.google.jwt.GoogleAccessTokenValidatorService;
import org.strac.service.google.jwt.GoogleAccessTokenValidatorServiceImpl;

@Configuration
@Import({StracExamDaoConfig.class})
public class StracExamServiceConfig {
    @Bean
    public GoogleDriveService googleDriveService(GoogleDriveDao googleDriveDao,
                                                 GoogleDriveCredentialService googleDriveCredentialService,
                                                 MultipartFileToFileTransformerService multipartFileToFileTransformerService) {
        return new GoogleDriveServiceImpl(googleDriveDao, googleDriveCredentialService, multipartFileToFileTransformerService);
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
    public GoogleAccessTokenValidatorService tokenValidatorService() {
        return new GoogleAccessTokenValidatorServiceImpl();
    }
}