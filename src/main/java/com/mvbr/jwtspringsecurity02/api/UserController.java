package com.mvbr.jwtspringsecurity02.api;

import com.mvbr.jwtspringsecurity02.api.dto.UserCreateRequest;
import com.mvbr.jwtspringsecurity02.domain.Role;
import com.mvbr.jwtspringsecurity02.domain.RoleRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import com.mvbr.jwtspringsecurity02.infrastructure.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Usuários", description = "Operações de administração de usuários (apenas ADMIN)")
@RestController
@RequestMapping("/api/v1/auth/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário. Apenas ADMIN pode acessar este endpoint.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody final UserCreateRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }

        Set<Role> roles = new HashSet<>();

        for (String roleName : request.roles()) {
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                return ResponseEntity.badRequest().body("Perfil não encontrado: " + roleName);
            }

            roles.add(role);
        }

        User userCreatedBy = getCurrentUser();

        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        user.setRoles(roles);
        user.setEnabled(false);
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setConfirmationTokenCreatedAt(java.time.LocalDateTime.now());
        user.setCreatedBy(userCreatedBy.getCreatedBy());

        userRepository.save(user);

        // Envia o e-mail de confirmação
        String confirmationLink = "http://localhost:8080/api/v1/auth/users/confirm?token=" + user.getConfirmationToken();
        emailService.sendConfirmationEmail(request.username(), confirmationLink);

        return ResponseEntity.ok("Usuário criado com sucesso. Confirme seu e-mail para ativar a conta.");
    }

    @Operation(summary = "Confirmar e-mail", description = "Confirma o cadastro do usuário através do token enviado por e-mail.")
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") final String token) {
        Optional<User> userOpt = userRepository.findByConfirmationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido.");
        }
        User user = userOpt.get();
        java.time.LocalDateTime createdAt = user.getConfirmationTokenCreatedAt();
        if (createdAt == null || java.time.Duration.between(createdAt, java.time.LocalDateTime.now()).toHours() > 24) {
            return ResponseEntity.badRequest().body("Token expirado. Solicite novo cadastro.");
        }
        user.setEnabled(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenCreatedAt(null);
        userRepository.save(user);
        return ResponseEntity.ok("Conta ativada com sucesso!");
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

}
