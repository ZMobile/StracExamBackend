package com.strac.service.drive;

import com.google.api.client.auth.oauth2.Credential;
import org.junit.jupiter.api.Test;
import org.strac.service.drive.GoogleDriveCredentialServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

public class GoogleDriveCredentialServiceTest {

    @Test
    void testCreateCredentialFromAccessToken() {
        // Arrange
        String accessToken = "mockAccessToken";

        // Instantiate the actual service
        GoogleDriveCredentialServiceImpl googleDriveCredentialService = new GoogleDriveCredentialServiceImpl();

        // Act
        Credential credential = googleDriveCredentialService.createCredentialFromAccessToken(accessToken);

        // Assert
        assertNotNull(credential, "Credential should not be null");
        assertEquals(accessToken, credential.getAccessToken(), "Access token in credential should match the one provided");
    }
}
