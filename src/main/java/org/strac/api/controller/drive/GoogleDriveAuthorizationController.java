package org.strac.api.controller.drive;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.strac.model.CredentialsResource;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class GoogleDriveAuthorizationController {
    @Autowired
    private Gson gson;

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
    public String handleCallback(@RequestParam("code") String code) {
        try {
            GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            // Generate a JWT containing the Google access token
            CredentialsResource credentialsResource = new CredentialsResource(googleTokenResponse.getAccessToken(), googleTokenResponse.getRefreshToken());
            return gson.toJson(credentialsResource);
        } catch (IOException e) {
            throw new RuntimeException("Error during OAuth 2.0 callback handling", e);
        }
    }

    @PostMapping("/refresh")
    public CredentialsResource refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        System.out.println("Refreshing...");
        String trimmedRefreshToken = refreshToken;
        if (refreshToken.startsWith("Bearer")) {
            trimmedRefreshToken = refreshToken.substring(7);
        }
        System.out.println("Trimmed refresh token: " + trimmedRefreshToken);
        try {

            // Exchange the refresh token for a new access token
            GoogleTokenResponse tokenResponse = googleAuthorizationCodeFlow
                    .newTokenRequest(null) // Pass null since we're refreshing, not exchanging an auth code
                    .setGrantType("refresh_token")
                    .setRefreshToken(trimmedRefreshToken) // Set the refresh token here
                    .execute();

            // Generate a new JWT containing the refreshed tokens
            return new CredentialsResource(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        } catch (IOException e) {
            throw new RuntimeException("Error refreshing access token", e);
        }
    }
}
