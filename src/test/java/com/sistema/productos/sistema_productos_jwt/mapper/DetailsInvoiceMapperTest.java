package com.sistema.productos.sistema_productos_jwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.entity.DetailsInvoice;
import com.sistema.productos.sistema_productos_jwt.entity.Product;
import com.sistema.productos.sistema_productos_jwt.exception.ProductNotFoundException;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;

class DetailsInvoiceMapperTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private DetailsInvoiceMapper detailsInvoiceMapper;

    private DetailsInvoice detailsInvoice;
    private DetailsInvoiceDto detailsInvoiceDto;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setIdProduct(1L);
        product.setName("Laptop");
        product.setPrice(java.math.BigDecimal.valueOf(1500000));
        product.setStock(10);
        product.setDescription("Laptop HP");

        productDto = new ProductDto();
        productDto.setIdProduct(1L);
        productDto.setName("Laptop");

        detailsInvoice = new DetailsInvoice();
        detailsInvoice.setId(1L);
        detailsInvoice.setPrice(50000L);
        detailsInvoice.setDescription("Compra de laptop");
        detailsInvoice.setProduct(product);

        detailsInvoiceDto = new DetailsInvoiceDto();
        detailsInvoiceDto.setPrice(50000L);
        detailsInvoiceDto.setDescription("Compra de laptop");
        detailsInvoiceDto.setProductDto(productDto);
    }

    @Test
    void testToEntity_Success() {
        when(productRepository.findByIdProduct(1L)).thenReturn(Optional.of(product));

        DetailsInvoice result = detailsInvoiceMapper.toEntity(detailsInvoiceDto);

        assertNotNull(result);
        assertEquals(detailsInvoiceDto.getPrice(), result.getPrice());
        assertEquals(detailsInvoiceDto.getDescription(), result.getDescription());
        assertNotNull(result.getProduct());
        assertEquals(product.getId(), result.getProduct().getId());
    }

    @Test
    void testToEntity_ProductNotFound() {
        when(productRepository.findByIdProduct(999L)).thenReturn(Optional.empty());
        detailsInvoiceDto.getProductDto().setIdProduct(999L);

        assertThrows(ProductNotFoundException.class, () -> {
            detailsInvoiceMapper.toEntity(detailsInvoiceDto);
        });
    }

    @Test
    void testToEntity_Null() {
        DetailsInvoice result = detailsInvoiceMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToDto_Success() {
        when(productMapper.toDto(product)).thenReturn(productDto);

        DetailsInvoiceDto result = detailsInvoiceMapper.toDto(detailsInvoice);

        assertNotNull(result);
        assertEquals(detailsInvoice.getPrice(), result.getPrice());
        assertEquals(detailsInvoice.getDescription(), result.getDescription());
        assertNotNull(result.getProductDto());
    }

    @Test
    void testToDto_Null() {
        DetailsInvoiceDto result = detailsInvoiceMapper.toDto(null);

        assertNull(result);
    }
}
