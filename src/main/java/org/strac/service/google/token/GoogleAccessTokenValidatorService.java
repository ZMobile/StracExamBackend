package org.strac.service.google.token;

public interface GoogleAccessTokenValidatorService {
    boolean validateGoogleAccessToken(String token);
}
