package org.strac.dao.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.gson.GsonFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.strac.dao.GoogleDriveDao;
import org.strac.dao.GoogleDriveDaoImpl;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;

@Configuration
public class StracExamDaoConfig {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String GOOGLE_CREDENTIALS_PATH = USER_HOME + "/documents/.dev/.strac/keys/google/credentials.json";
    private static final String FRONTEND_REDIRECT_URL = "http://localhost:8081/oauth2/callback";

    @Bean
    public GoogleClientSecrets googleClientSecrets(JsonFactory jsonFactory) {
        try (FileReader reader = new FileReader(Paths.get(GOOGLE_CREDENTIALS_PATH).toFile())) {
            return GoogleClientSecrets.load(jsonFactory, reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Google client secrets from credentials.json", e);
        }
    }

    @Bean
    public JsonFactory jsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public NetHttpTransport netHttpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(NetHttpTransport netHttpTransport,
                                                                   JsonFactory jsonFactory,
                                                                   GoogleClientSecrets googleClientSecrets) {
        return new GoogleAuthorizationCodeFlow.Builder(
                netHttpTransport,
                jsonFactory,
                googleClientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/drive.file")
        )
                .setAccessType("offline") // Needed for refresh tokens
                .build();
    }

    @Bean
    public String redirectUri() {
        return FRONTEND_REDIRECT_URL;
    }

    @Bean
    public GoogleDriveDao googleDriveDao() {
        return new GoogleDriveDaoImpl();
    }
}
