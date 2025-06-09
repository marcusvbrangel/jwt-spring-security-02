package com.mvbr.jwtspringsecurity02.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvbr.jwtspringsecurity02.api.dto.AuthRequest;
import com.mvbr.jwtspringsecurity02.api.dto.AuthResponse;
import com.mvbr.jwtspringsecurity02.domain.Role;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import com.mvbr.jwtspringsecurity02.infrastructure.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_invalidCredentials_shouldReturnUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest("invalid", "invalid");
        Mockito.when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_validCredentials_shouldReturnOk() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("admin");
        user.setPassword("encoded");
        user.setRoles(Set.of(new Role("ADMIN")));
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(Mockito.anyString(), Mockito.any(UUID.class))).thenReturn("token");
        AuthRequest request = new AuthRequest("admin", "admin");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

