package org.strac.dao.token;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAccessTokenRefreshDaoImpl implements GoogleAccessTokenRefreshDao {
    private final GoogleClientSecrets googleClientSecrets;
    private final NetHttpTransport netHttpTransport;
    private final JsonFactory jsonFactory;

    public GoogleAccessTokenRefreshDaoImpl(GoogleClientSecrets googleClientSecrets,
                                           NetHttpTransport netHttpTransport,
                                           JsonFactory jsonFactory) {
        this.googleClientSecrets = googleClientSecrets;
        this.netHttpTransport = netHttpTransport;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            // Build the token request payload
            Map<String, String> parameters = new HashMap<>();
            parameters.put("client_id", googleClientSecrets.getDetails().getClientId());
            parameters.put("client_secret", googleClientSecrets.getDetails().getClientSecret());
            parameters.put("refresh_token", refreshToken);
            parameters.put("grant_type", "refresh_token");

            // Make the HTTP POST request
            HttpRequestFactory requestFactory = netHttpTransport.createRequestFactory(request -> request.setParser(jsonFactory.createJsonObjectParser()));

            GoogleTokenResponse tokenResponse = requestFactory
                    .buildPostRequest(new GenericUrl("https://oauth2.googleapis.com/token"),
                            new UrlEncodedContent(parameters))
                    .execute()
                    .parseAs(GoogleTokenResponse.class);
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing access token", e);
        }
    }
}
