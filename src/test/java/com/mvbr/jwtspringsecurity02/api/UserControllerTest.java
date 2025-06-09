package com.mvbr.jwtspringsecurity02.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvbr.jwtspringsecurity02.api.dto.UserCreateRequest;
import com.mvbr.jwtspringsecurity02.domain.Role;
import com.mvbr.jwtspringsecurity02.domain.RoleRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_asAdmin_shouldReturnOk() throws Exception {
        UserCreateRequest request = new UserCreateRequest("novo", "senha", Set.of("USER"));
        Mockito.when(userRepository.existsByUsername("novo")).thenReturn(false);
        Mockito.when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role("USER")));
        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_userAlreadyExists_shouldReturnBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest("existente", "senha", Set.of("USER"));
        Mockito.when(userRepository.existsByUsername("existente")).thenReturn(true);
        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_roleNotFound_shouldReturnBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest("novo", "senha", Set.of("INEXISTENTE"));
        Mockito.when(userRepository.existsByUsername("novo")).thenReturn(false);
        Mockito.when(roleRepository.findByName("INEXISTENTE")).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_unauthenticated_shouldReturnUnauthorized() throws Exception {
        UserCreateRequest request = new UserCreateRequest("novo", "senha", Set.of("USER"));
        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createUser_asUser_shouldReturnForbidden() throws Exception {
        UserCreateRequest request = new UserCreateRequest("novo", "senha", Set.of("USER"));
        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}

