package org.strac.service.google.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Repository
public class GoogleAccessTokenValidatorServiceImpl implements GoogleAccessTokenValidatorService {
    public boolean validateGoogleAccessToken(String token) {
        try {
            // Google JWKS URL
            String jwksUrl = "https://www.googleapis.com/oauth2/v3/certs";

            // Fetch JWKS and parse keys
            // (Consider caching the keys for better performance)
            JwkProvider provider = new UrlJwkProvider(new URL(jwksUrl));
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();



            // Verify the JWT
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://accounts.google.com")
                    .build();
            verifier.verify(token);

            // If no exceptions, token is valid
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
