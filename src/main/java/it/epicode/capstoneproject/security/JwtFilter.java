package it.epicode.capstoneproject.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.ErrorResponse;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import it.epicode.capstoneproject.services.AuthService;
import it.epicode.capstoneproject.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private AuthService authSvc;
    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = req.getHeader("Authorization");
            if (authorization == null)
                throw new UnauthorizedException("Access token assente");
            else if (!authorization.startsWith("Bearer "))
                throw new UnauthorizedException("Access token non valido");

            String token = authorization.split(" ")[1];

            // Log the token
            System.out.println("Received token: " + token);

            Long userId = jwtTools.extractUserIdFromToken(token);

            // Log the extracted user ID
            System.out.println("Extracted user ID: " + userId);

            if (userId == null) {
                throw new UnauthorizedException("User ID non valido");
            }

            // Fetch user from UserService
            Optional<User> userOptional = userService.getUserById(userId);
            if (userOptional.isEmpty()) {
                throw new UnauthorizedException("User not found");
            }
            User u = userOptional.get();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(req, res);
        } catch (UnauthorizedException e) {
            ObjectMapper mapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write(mapper.writeValueAsString(
                    new ErrorResponse(HttpStatus.UNAUTHORIZED,
                            "Unauthorized", e.getMessage()
                    )));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        return new AntPathMatcher().match("/api/auth/**", req.getServletPath())
                || new AntPathMatcher().match("/api/users/getAll", req.getServletPath());
    }


}


