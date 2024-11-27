package org.strac.service.google.drive;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import org.springframework.stereotype.Service;

@Service
public class GoogleDriveCredentialServiceImpl implements GoogleDriveCredentialService {
    /**
     * Create a Google API Credential from an access token.
     *
     * @param accessToken The access token.
     * @return A Credential object for accessing Google APIs.
     */
    public Credential createCredentialFromAccessToken(String accessToken) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new com.google.api.client.http.javanet.NetHttpTransport())
                .setJsonFactory(new com.google.api.client.json.gson.GsonFactory())
                .build()
                .setAccessToken(accessToken);
    }
}
