package org.strac.service.google.jwt;

public interface GoogleAccessTokenValidatorService {
    boolean validateGoogleAccessToken(String token);
}
