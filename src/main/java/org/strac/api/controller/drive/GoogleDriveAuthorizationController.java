package org.strac.api.controller.drive;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.strac.model.CredentialsResource;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class GoogleDriveAuthorizationController {
    @Autowired
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Autowired
    private String redirectUri;

    @GetMapping("/auth")
    public String authorizeUser() {
        return googleAuthorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    @GetMapping("/callback")
    public CredentialsResource handleCallback(@RequestParam("code") String code) {
        try {
            GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            System.out.println("googleTokenResponse.getAccessToken() = " + googleTokenResponse.getAccessToken());
            System.out.println("googleTokenResponse.getRefreshToken() = " + googleTokenResponse.getRefreshToken());
            // Generate a JWT containing the Google access token
            return new CredentialsResource(googleTokenResponse.getAccessToken(), googleTokenResponse.getRefreshToken());
        } catch (IOException e) {
            throw new RuntimeException("Error during OAuth 2.0 callback handling", e);
        }
    }

    @PostMapping("/refresh")
    public CredentialsResource refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            // Exchange the refresh token for a new access token
            GoogleTokenResponse tokenResponse = googleAuthorizationCodeFlow.newTokenRequest(refreshToken)
                    .setGrantType("refresh_token")
                    .execute();

            // Generate a new JWT containing the refreshed tokens
            return new CredentialsResource(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        } catch (IOException e) {
            throw new RuntimeException("Error refreshing access token", e);
        }
    }
}
