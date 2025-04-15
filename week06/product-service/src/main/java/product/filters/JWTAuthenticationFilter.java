package product.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final String SECRET_KEY = "mysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstring";  // Your secret key

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        System.out.println(token);
        if (token != null) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                System.out.println(claims.getSubject());

                // Create authorities list from claims
                List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

                System.out.println("authorities: " + authorities);

                // Create an Authentication object
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities
                );

                // Create an empty SecurityContext and set the authentication object
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

            } catch (Exception e) {
                // Handle token parsing exceptions or invalid token
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof List) {
            List<String> roles = ((List<?>) rolesObject).stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> ((Map<?, ?>) item).get("authority"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());

            System.out.println(roles);

            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            System.out.println("Roles are not in the expected format");
            return Collections.emptyList();
        }
    }

}