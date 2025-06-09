package com.mvbr.jwtspringsecurity02.api;

import com.mvbr.jwtspringsecurity02.api.dto.ProductCreateRequest;
import com.mvbr.jwtspringsecurity02.api.dto.ProductResponse;
import com.mvbr.jwtspringsecurity02.api.dto.ProductUpdateRequest;
import com.mvbr.jwtspringsecurity02.domain.Product;
import com.mvbr.jwtspringsecurity02.domain.ProductRepository;
import com.mvbr.jwtspringsecurity02.domain.User;
import com.mvbr.jwtspringsecurity02.domain.UserRepository;
import com.mvbr.jwtspringsecurity02.api.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Produtos", description = "Operações relacionadas a produtos")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Operation(summary = "Criar produto", description = "Cria um novo produto para o usuário autenticado.")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        User user = getCurrentUser();
        Product product = new Product(request.nome(), request.preco(), user);
        productRepository.save(product);
        logger.info("Produto criado pelo usuário {}: {}", user.getUsername(), product.getId());
        return ResponseEntity.ok(toResponse(product));
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza um produto do usuário autenticado.")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        User user = getCurrentUser();
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isEmpty()) {
            logger.warn("Tentativa de alterar produto inexistente: {} por {}", id, user.getUsername());
            throw new BusinessException("Produto não encontrado");
        }
        Product product = optProduct.get();
        if (!product.getDono().getId().equals(user.getId())) {
            logger.warn("Violação de acesso: usuário {} tentou alterar produto de {}", user.getUsername(), product.getDono().getUsername());
            throw new BusinessException("Acesso negado: você não é o dono deste produto");
        }
        product.setNome(request.nome());
        product.setPreco(request.preco());
        productRepository.save(product);
        return ResponseEntity.ok(toResponse(product));
    }

    @Operation(summary = "Excluir produto", description = "Exclui um produto do usuário autenticado.")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        User user = getCurrentUser();
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isEmpty()) {
            logger.warn("Tentativa de excluir produto inexistente: {} por {}", id, user.getUsername());
            throw new BusinessException("Produto não encontrado");
        }
        Product product = optProduct.get();
        if (!product.getDono().getId().equals(user.getId())) {
            logger.warn("Violação de acesso: usuário {} tentou excluir produto de {}", user.getUsername(), product.getDono().getUsername());
            throw new BusinessException("Acesso negado: você não é o dono deste produto");
        }
        productRepository.delete(product);
        logger.info("Produto removido pelo usuário {}: {}", user.getUsername(), id);
        return ResponseEntity.ok("Produto removido");
    }

    @Operation(summary = "Listar produtos", description = "Lista todos os produtos se ADMIN, ou apenas os do usuário se USER.")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts() {
        User user = getCurrentUser();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        List<ProductResponse> products;
        if (isAdmin) {
            products = productRepository.findAll()
                    .stream().map(this::toResponse).collect(Collectors.toList());
        } else {
            products = productRepository.findByDonoId(user.getId())
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Endpoint público", description = "Endpoint público acessível a todos.")
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Endpoint público acessível a todos");
    }

    @Operation(summary = "Endpoint denyAll", description = "Endpoint que nega acesso a todos.")
    @GetMapping("/deny")
    public ResponseEntity<String> denyAllEndpoint() {
        return ResponseEntity.status(403).body("Acesso negado a todos");
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getNome(),
                product.getPreco(),
                product.getDono().getId(),
                product.getDono().getUsername()
        );
    }
}
