package com.mvbr.jwtspringsecurity02.api;

import com.mvbr.jwtspringsecurity02.api.dto.UserCreateRequest;
import com.mvbr.jwtspringsecurity02.domain.Role;
import com.mvbr.jwtspringsecurity02.domain.RoleRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Tag(name = "Usuários", description = "Operações de administração de usuários (apenas ADMIN)")
@RestController
@RequestMapping("/api/v1/auth/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário. Apenas ADMIN pode acessar este endpoint.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest request) {
        // Não precisa validar username manualmente, pois o Bean Validation já faz isso
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
        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("Usuário criado com sucesso");
    }
}
