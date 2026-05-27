package com.sistema.productos.sistema_productos_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistema.productos.sistema_productos_jwt.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Optional<Product> findByStock(Integer stock);

    boolean existsByName(String name);

    Optional<Product> findByIdProduct(Long idProduct);

}
