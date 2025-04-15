package iuh.fit.se.identity.services;

import iuh.fit.se.identity.dtos.SignInRequest;
import iuh.fit.se.identity.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> signIn(SignInRequest signInRequest);

    ResponseEntity<?> signUp(SignUpRequest signUpRequest);
}
