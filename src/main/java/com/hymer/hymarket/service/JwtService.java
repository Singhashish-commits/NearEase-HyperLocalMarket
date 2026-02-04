package com.hymer.hymarket.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secretKey;
    // Secret Key
    public JwtService(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = secretKey;
    }

    // Secret Key Generation passed into getKey() and then passed into generateToken
    // currently we are using the hardcoded secret key

    // we are not using the generate key currently
    public String generateKey() { // it generates secret key // Reason we are not using it is bcz it generate the random keay each time
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

            SecretKey secretKey = keyGen.generateKey();
            System.out.println("secretKey: " + secretKey.toString());

            byte[] keyBytes = secretKey.getEncoded();

            return Base64.getEncoder().encodeToString(keyBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to find HmacSHA256 algorithm", e);
        }
    }


    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return  Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(getKey()).compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
// the methods extractUserName is calling the extractClaims and which calls the extractAllClaims all these three work together
    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);

    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith((SecretKey) getKey())
//                .build().parseClaimsJws(token).getBody();

        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

// validate the token used in jwtFilter class and pass the detail to the usernamePasswordAuthenticator
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
// checking the expiration time of teh token
    private boolean isTokenExpired(String token) {
         return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
