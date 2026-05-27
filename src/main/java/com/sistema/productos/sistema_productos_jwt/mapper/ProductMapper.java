package com.sistema.productos.sistema_productos_jwt.mapper;

import org.springframework.stereotype.Component;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }
        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStock(dto.getStock());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setIdProduct(dto.getIdProduct());
        return entity;
    }

    public ProductDto toDto(Product entity) {
        if (entity == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStock(entity.getStock());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setIdProduct(entity.getIdProduct());
        return dto;
    }
}
