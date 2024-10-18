package com.example.shopapp.components;

import com.example.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    // the expiration time of the token in milliseconds, retrieved from the application.properties file
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(User user) {
        // The properties of the user that will be included in the token are called claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
        // generate a token for the user
        // return the generated token
        try {
            // We have to create a separate variable to store the token for debugging purposes
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(user.getPhoneNumber())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey())
                    .compact();
            return token;
        } catch (Exception e) {
            // Instead of printing the error message, you should log it
            // Will add logger later
            System.err.println("Cannot generate token, error: " + e.getMessage());
            return null;
        }
    }

    private SecretKey getSignInKey() {
        // generate a key from the secret key
        // return the generated key
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private Claims extractAllClaims(String token) {
        // https://stackoverflow.com/a/77408683
        // The syntax of the Jwts.parser() method has changed in the latest version of the jjwt library
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // extract a claim from the token
        // return the extracted claim
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // check expiration
    public boolean isTokenExpired(String token) {
        // extract the expiration date from the token
        // check if the expiration date is before the current date
        // return the result of the check
        Date expirationDate = (Date) this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
}
