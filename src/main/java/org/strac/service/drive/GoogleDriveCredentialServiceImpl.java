package org.strac.service.drive;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import org.springframework.stereotype.Service;

@Service
public class GoogleDriveCredentialServiceImpl implements GoogleDriveCredentialService {
    public Credential createCredentialFromAccessToken(String accessToken) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new com.google.api.client.http.javanet.NetHttpTransport())
                .setJsonFactory(new com.google.api.client.json.gson.GsonFactory())
                .build()
                .setAccessToken(accessToken);
    }
}
