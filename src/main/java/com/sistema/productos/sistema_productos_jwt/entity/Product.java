package com.sistema.productos.sistema_productos_jwt.entity;

import java.math.BigDecimal;

import com.sistema.productos.sistema_productos_jwt.auth.AuthEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AuthEntity auth = new AuthEntity();

    private Long idProduct;

    private String name;

    private Integer stock;

    private String description;

    private BigDecimal price;

}
