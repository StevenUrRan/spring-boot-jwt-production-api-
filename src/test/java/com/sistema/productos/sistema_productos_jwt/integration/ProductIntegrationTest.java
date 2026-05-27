package com.sistema.productos.sistema_productos_jwt.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateAndRetrieveProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(10L);
        productDto.setName("Laptop HP");
        productDto.setStock(10);
        productDto.setDescription("Laptop HP 15 pulgadas");
        productDto.setPrice(java.math.BigDecimal.valueOf(500000));

        // Crear producto
        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop HP"));

        // Recuperar todos los productos
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop HP"));

        // Buscar por nombre
        mockMvc.perform(get("/product/Laptop HP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(500000));
    }

    @Test
    void testCreateUpdateAndDeleteProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(20L);
        productDto.setName("Mouse Logitech");
        productDto.setStock(50);
        productDto.setDescription("Mouse inalámbrico");
        productDto.setPrice(java.math.BigDecimal.valueOf(250000));

        // Crear producto
        var response = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        Long productId = objectMapper.readTree(jsonResponse).get("id").asLong();

        // Actualizar producto
        ProductDto updatedDto = new ProductDto();
        updatedDto.setIdProduct(20L);
        updatedDto.setName("Mouse Logitech MX");
        updatedDto.setStock(30);
        updatedDto.setDescription("Mouse inalámbrico actualizado");
        updatedDto.setPrice(java.math.BigDecimal.valueOf(350000));

        mockMvc.perform(put("/product/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        // Eliminar producto
        mockMvc.perform(delete("/product/" + productId))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/product/" + productId))
                .andExpect(status().isNotFound());
    }
}
