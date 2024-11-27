package org.strac.service.google.token;

public interface GoogleAccessTokenRefreshService {
    String refreshAccessToken(String refreshToken);
}
