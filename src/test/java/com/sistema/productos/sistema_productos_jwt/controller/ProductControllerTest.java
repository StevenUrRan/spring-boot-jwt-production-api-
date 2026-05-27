package com.sistema.productos.sistema_productos_jwt.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.exception.ProductNotFoundException;
import com.sistema.productos.sistema_productos_jwt.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setIdProduct(10L);
        productDto.setName("Laptop HP");
        productDto.setStock(10);
        productDto.setDescription("Laptop HP 15 pulgadas");
        productDto.setPrice(java.math.BigDecimal.valueOf(500000));
    }

    @Test
    void testFindAll() throws Exception {
        ProductDto product2 = new ProductDto();
        product2.setIdProduct(20L);
        product2.setName("Mouse");

        List<ProductDto> products = Arrays.asList(productDto, product2);
        org.springframework.data.domain.Page<ProductDto> page = new org.springframework.data.domain.PageImpl<>(products);
        when(productService.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop HP"))
                .andExpect(jsonPath("$.content[1].name").value("Mouse"));
    }

    @Test
    void testFindByName_Success() throws Exception {
        when(productService.findByName("Laptop HP")).thenReturn(productDto);

        mockMvc.perform(get("/product/Laptop HP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop HP"))
                .andExpect(jsonPath("$.price").value(500000));
    }

    @Test
    void testFindByName_NotFound() throws Exception {
        when(productService.findByName("NoExiste")).thenThrow(new ProductNotFoundException());

        mockMvc.perform(get("/product/NoExiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNewProduct_Success() throws Exception {
        when(productService.newProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop HP"));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        when(productService.updateProduct(any(ProductDto.class), anyLong())).thenReturn(productDto);

        mockMvc.perform(put("/product/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop HP"));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/product/1"))
                .andExpect(status().isOk());
    }
}
