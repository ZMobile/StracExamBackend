package org.strac.model;

public class CredentialsResource {
    private String accessToken;
    private String refreshToken;

    public CredentialsResource(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
