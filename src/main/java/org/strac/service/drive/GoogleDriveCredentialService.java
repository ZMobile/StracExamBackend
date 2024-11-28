package org.strac.service.drive;

import com.google.api.client.auth.oauth2.Credential;

public interface GoogleDriveCredentialService {
    /**
     * Create a Google API Credential from an access token.
     *
     * @param accessToken The access token.
     * @return A Credential object for accessing Google APIs.
     */
    Credential createCredentialFromAccessToken(String accessToken);
}
