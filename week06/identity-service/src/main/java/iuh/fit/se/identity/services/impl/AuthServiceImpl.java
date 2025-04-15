package iuh.fit.se.identity.services.impl;

import iuh.fit.se.identity.dtos.SignInRequest;
import iuh.fit.se.identity.dtos.SignInResponse;
import iuh.fit.se.identity.dtos.SignUpRequest;
import iuh.fit.se.identity.entities.User;
import iuh.fit.se.identity.repositories.UserRepository;
import iuh.fit.se.identity.services.AuthService;
import iuh.fit.se.identity.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> signIn(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authencation: {}", authentication);

        String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(new SignInResponse(token));
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + signUpRequest.getUsername() + "' already exists.");
        }

        User newUser = new User();
        newUser.setUsername(signUpRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setRoles(signUpRequest.getRoles());

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }
}
