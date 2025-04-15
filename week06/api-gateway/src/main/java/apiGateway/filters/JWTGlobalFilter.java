package apiGateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JWTGlobalFilter implements WebFilter {
    private static final String SECRET_KEY = "mysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstringmysecretstring";

    private String extractJwtFromrequest(ServerWebExchange exchange){
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims extractClaims(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (JwtException e){
            return null;
        }
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaims(Claims claims){
        Object rolesObject = claims.get("roles");
        if(rolesObject instanceof List){
            List<String> roles = ((List<?>) rolesObject).stream()
                    .filter(o -> o instanceof Map)
                    .map(item -> ((Map) item).get("authority"))
                    .filter(Objects::nonNull)
                    .map(Objects::toString)
                    .collect(Collectors.toList());

            return roles.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }else {
            System.out.println("Roles are not in the excepted format");
            return Collections.emptyList();
        }
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if(exchange.getRequest().getURI().toString().contains("/auth/login") ||
            exchange.getRequest().getURI().toString().contains("/auth/register")
        ){
            return chain.filter(exchange);
        }

        String token = extractJwtFromrequest(exchange);
        Claims claims = extractClaims(token);
        if(token == null || claims == null){
            return Mono.error(new JwtException("Invalid or missing JWT token"));
        }

        List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null, authorities
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        exchange.getRequest().mutate().header("Authorization", "Bearer " + token);

        return exchange.getSession()
                .flatMap(webSession -> {
                    webSession.getAttributes().put("SPRING_SECURITY_CONTEXT", securityContext);
                    return chain.filter(exchange);
                });
    }
}
