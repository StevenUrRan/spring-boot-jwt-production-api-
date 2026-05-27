package com.sistema.productos.sistema_productos_jwt.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.entity.Product;
import com.sistema.productos.sistema_productos_jwt.exception.NameProductExistException;
import com.sistema.productos.sistema_productos_jwt.exception.ProductNotFoundException;
import com.sistema.productos.sistema_productos_jwt.mapper.ProductMapper;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStock(10);
        product.setDescription("Laptop HP 15 pulgadas");
        product.setPrice(java.math.BigDecimal.valueOf(1500000));

        productDto = new ProductDto();
        productDto.setIdProduct(1L);
        productDto.setName("Laptop");
        productDto.setStock(10);
        productDto.setDescription("Laptop HP 15 pulgadas");
        productDto.setPrice(java.math.BigDecimal.valueOf(1500000));
    }

    @Test
    void testFindAll() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");

        List<Product> products = Arrays.asList(product, product2);
        org.springframework.data.domain.Page<Product> page = new org.springframework.data.domain.PageImpl<>(products);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toDto(product)).thenReturn(productDto);
        when(productMapper.toDto(product2)).thenReturn(productDto);

        org.springframework.data.domain.Page<ProductDto> result = productService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindByName_Success() {
        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.findByName("Laptop");

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findByName("Laptop");
    }

    @Test
    void testFindByName_NotFound() {
        when(productRepository.findByName("NoExiste")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.findByName("NoExiste");
        });

        verify(productRepository, times(1)).findByName("NoExiste");
    }

    @Test
    void testNewProduct_Success() {
        when(productRepository.existsByName("Laptop")).thenReturn(false);
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.newProduct(productDto);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).existsByName("Laptop");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testNewProduct_AlreadyExists() {
        when(productRepository.existsByName("Laptop")).thenReturn(true);

        assertThrows(NameProductExistException.class, () -> {
            productService.newProduct(productDto);
        });

        verify(productRepository, times(1)).existsByName("Laptop");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        ProductDto updateDto = new ProductDto();
        updateDto.setName("Laptop Actualizada");
        updateDto.setStock(20);
        updateDto.setDescription("Nueva descripción");
        updateDto.setPrice(java.math.BigDecimal.valueOf(1800000));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toEntity(updateDto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(updateDto, 1L);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(productDto, 999L);
        });

        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        verify(productRepository, times(1)).findById(999L);
    }
}
