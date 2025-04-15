package iuh.fit.se.identity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JWTUtil {
    private static final String SECRET_KEY = "mysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstring";

    private static final long EXPIRATION_TIME = 864_000_000L;

    public static String generateToken(UserDetails userDetails){
        var t = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        log.info("Toekn: {}", t);

        return t;
    }

    private static Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (userDetails.getUsername().equals(getUsernameFromToken(token)) && !isTokenExpired(token));
    }
}
