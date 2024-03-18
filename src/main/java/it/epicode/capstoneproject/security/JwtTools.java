package it.epicode.capstoneproject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@PropertySource("application.properties")
public class JwtTools {
    @Value("${access_token.secret}")
    private String secret;

    @Value("${access_token.expiresIn}")
    private long exp;




    public String createToken(User u, String exp) {
        long expirationMillis;
        try {
            expirationMillis = System.currentTimeMillis() + Long.parseLong(exp);
        } catch (NumberFormatException e) {
            // Handle the case where exp is not a valid numeric value
            throw new IllegalArgumentException("Invalid expiration time: " + exp, e);
        }

        Date expirationDate = new Date(expirationMillis); // Convert expiration to a Date object
        return Jwts.builder()
                .setSubject(u.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate) // Pass the expiration Date object
                .signWith(Keys.hmacShaKeyFor(secret.getBytes())) // Use the secret field
                .compact();
    }

    public void validateToken(String token) throws UnauthorizedException {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build().parse(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Access token is not vawefdfewqflid");
        }
    }

    public Long extractUserIdFromToken(String token) throws UnauthorizedException {
        try {
            return Long.parseLong(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build()
                    .parseSignedClaims(token).getPayload().getSubject());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Access token is not valid");
        }
    }

    public boolean matchTokenSub(Long userId) throws UnauthorizedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest req;
        if (requestAttributes instanceof ServletRequestAttributes) {
            req = ((ServletRequestAttributes) requestAttributes).getRequest();
        } else {
            return false;
        }
        String token = req.getHeader("Authorization").split(" ")[1];
        Long tokenUserId = extractUserIdFromToken(token);
        return tokenUserId.equals(userId);
    }

    public Long extractUserIdFromReq() throws UnauthorizedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest req;
        if (requestAttributes instanceof ServletRequestAttributes) {
            req = ((ServletRequestAttributes) requestAttributes).getRequest();
        } else
            throw new UnauthorizedException("Access token is not valid");
        String token = req.getHeader("Authorization").split(" ")[1];
        return extractUserIdFromToken(token);
    }
}
