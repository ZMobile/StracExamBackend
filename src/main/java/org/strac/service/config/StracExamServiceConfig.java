package org.strac.service.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.strac.dao.GoogleDriveDao;
import org.strac.dao.config.StracExamDaoConfig;
import org.strac.service.file.ZipService;
import org.strac.service.file.ZipServiceImpl;
import org.strac.service.drive.GoogleDriveCredentialService;
import org.strac.service.drive.GoogleDriveCredentialServiceImpl;
import org.strac.service.drive.GoogleDriveService;
import org.strac.service.drive.GoogleDriveServiceImpl;
import org.strac.service.file.MultiPartFileToFileTransformerServiceImpl;
import org.strac.service.file.MultipartFileToFileTransformerService;
import org.strac.dao.token.GoogleAccessTokenRefreshDao;
import org.strac.dao.token.GoogleAccessTokenRefreshDaoImpl;
import org.strac.dao.token.GoogleAccessTokenValidatorDao;
import org.strac.dao.token.GoogleAccessTokenValidatorDaoImpl;

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
    public GoogleAccessTokenValidatorDao tokenValidatorService() {
        return new GoogleAccessTokenValidatorDaoImpl();
    }

    @Bean
    public GoogleAccessTokenRefreshDao tokenRefreshService(GoogleClientSecrets googleClientSecrets,
                                                           NetHttpTransport netHttpTransport,
                                                           JsonFactory jsonFactory) {
        return new GoogleAccessTokenRefreshDaoImpl(googleClientSecrets, netHttpTransport, jsonFactory);
    }
}
