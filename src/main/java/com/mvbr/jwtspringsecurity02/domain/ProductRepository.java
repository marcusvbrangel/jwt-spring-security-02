package com.mvbr.jwtspringsecurity02.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByDonoId(UUID donoId);

    @Query("SELECT p FROM Product p JOIN FETCH p.dono")
    List<Product> findAllWithDono();

    @Query("SELECT p FROM Product p JOIN FETCH p.dono WHERE p.dono.id = :donoId")
    List<Product> findByDonoIdWithDono(@Param("donoId") UUID donoId);
}
