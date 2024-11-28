package org.strac.dao.token;

public interface GoogleAccessTokenRefreshDao {
    /**
     * Refresh a Google access token using a refresh token.
     *
     * @param refreshToken The refresh token to use.
     * @return The new access token.
     */
    String refreshAccessToken(String refreshToken);
}
