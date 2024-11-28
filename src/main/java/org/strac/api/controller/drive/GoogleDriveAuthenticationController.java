package org.strac.api.controller.drive;


import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.strac.model.CredentialsResource;
import org.strac.dao.token.GoogleAccessTokenRefreshDao;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class GoogleDriveAuthenticationController {
    @Autowired
    private Gson gson;

    @Autowired
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    @Autowired
    private String redirectUri;

    @Autowired
    private GoogleAccessTokenRefreshDao googleAccessTokenRefreshDao;

    @GetMapping("/auth")
    public String authorizeUser() {
        return googleAuthorizationCodeFlow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code) {
        try {
            GoogleAuthorizationCodeTokenRequest googleAuthorizationCodeTokenRequest = googleAuthorizationCodeFlow.newTokenRequest(code);
            GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeTokenRequest
                    .setRedirectUri(redirectUri)
                    .execute();
            CredentialsResource credentialsResource = new CredentialsResource(googleTokenResponse.getAccessToken(), googleTokenResponse.getRefreshToken());
            return gson.toJson(credentialsResource);
        } catch (IOException e) {
            throw new RuntimeException("Error during OAuth 2.0 callback handling", e);
        }
    }

    @PostMapping("/refresh")
    public String refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        String trimmedRefreshToken = refreshToken;
        if (refreshToken.startsWith("Bearer")) {
            trimmedRefreshToken = refreshToken.substring(7);
        }

        String newAccessToken = googleAccessTokenRefreshDao.refreshAccessToken(trimmedRefreshToken);
        CredentialsResource credentialsResource = new CredentialsResource(newAccessToken, trimmedRefreshToken);
        return gson.toJson(credentialsResource);
    }
}
