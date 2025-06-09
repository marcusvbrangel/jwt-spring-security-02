package com.mvbr.jwtspringsecurity02.api;

import com.mvbr.jwtspringsecurity02.api.dto.AuthRequest;
import com.mvbr.jwtspringsecurity02.api.dto.AuthResponse;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import com.mvbr.jwtspringsecurity02.infrastructure.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "Autenticação", description = "Operações de login e autenticação JWT")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(request.username()).orElseThrow();
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            String[] roles = user.getRoles().stream().map(r -> r.getName()).toArray(String[]::new);
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), roles));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
