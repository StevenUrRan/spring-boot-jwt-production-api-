package com.sistema.productos.sistema_productos_jwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.entity.Product;

class ProductMapperTest {

    private ProductMapper productMapper;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStock(10);
        product.setDescription("Laptop HP 15");
        product.setPrice(java.math.BigDecimal.valueOf(1500000));

        productDto = new ProductDto();
        productDto.setIdProduct(1L);
        productDto.setName("Laptop");
        productDto.setStock(10);
        productDto.setDescription("Laptop HP 15");
        productDto.setPrice(java.math.BigDecimal.valueOf(1500000));
    }

    @Test
    void testToEntity_Success() {
        Product result = productMapper.toEntity(productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        assertEquals(productDto.getStock(), result.getStock());
        assertEquals(productDto.getDescription(), result.getDescription());
        assertEquals(productDto.getPrice(), result.getPrice());
    }

    @Test
    void testToEntity_Null() {
        Product result = productMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToDto_Success() {
        ProductDto result = productMapper.toDto(product);

        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void testToDto_Null() {
        ProductDto result = productMapper.toDto(null);

        assertNull(result);
    }
}
