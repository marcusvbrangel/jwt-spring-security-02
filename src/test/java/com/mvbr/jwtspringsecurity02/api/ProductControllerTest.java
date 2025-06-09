package com.mvbr.jwtspringsecurity02.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvbr.jwtspringsecurity02.api.dto.ProductCreateRequest;
import com.mvbr.jwtspringsecurity02.domain.ProductRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("password");
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createProduct_asUser_shouldReturnOk() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("Produto Teste", BigDecimal.valueOf(10.0));
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_unauthenticated_shouldReturnUnauthorized() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("Produto Teste", BigDecimal.valueOf(10.0));
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

