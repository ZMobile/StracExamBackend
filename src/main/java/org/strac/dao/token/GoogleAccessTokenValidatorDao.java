package org.strac.dao.token;

public interface GoogleAccessTokenValidatorDao {
    /**
     * Validate a Google access token.
     *
     * @param token The access token to validate.
     * @return True if the token is valid, false otherwise.
     */
    boolean validateGoogleAccessToken(String token);
}
