package org.strac.service.google.token;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Repository
public class GoogleAccessTokenValidatorServiceImpl implements GoogleAccessTokenValidatorService {

    private static final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";

    @Override
    public boolean validateGoogleAccessToken(String token) {
        try {
            // Construct the token info URL
            URL url = new URL(TOKEN_INFO_URL + token);

            // Open HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Token is valid
                return true;
            } else {
                // Log the error response (e.g., invalid or expired token)
                Scanner scanner = new Scanner(connection.getErrorStream());
                while (scanner.hasNextLine()) {
                    System.err.println(scanner.nextLine());
                }
                scanner.close();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
