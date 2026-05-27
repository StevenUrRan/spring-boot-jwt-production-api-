package com.sistema.productos.sistema_productos_jwt.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.entity.Product;
import com.sistema.productos.sistema_productos_jwt.entity.User;
import com.sistema.productos.sistema_productos_jwt.repository.InvoiceRepository;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;
import com.sistema.productos.sistema_productos_jwt.repository.RoleRepository;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InvoiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Crear usuario de prueba
        testUser = new User();
        testUser.setUsername("usuario123");
        testUser.setEmail("usuario@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setEnable(true);
        testUser.setAdmin(false);
        testUser.setRoles(new HashSet<>());
        userRepository.save(testUser);

        // Crear producto de prueba
        testProduct = new Product();
        testProduct.setIdProduct(10L);
        testProduct.setName("Laptop");
        testProduct.setStock(10);
        testProduct.setDescription("Laptop HP 15 pulgadas");
        testProduct.setPrice(java.math.BigDecimal.valueOf(1500000));
        productRepository.save(testProduct);
    }

    @Test
    void testCreateAndRetrieveInvoice() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(testProduct.getIdProduct());

        DetailsInvoiceDto detailsDto = new DetailsInvoiceDto();
        detailsDto.setPrice(50000L);
        detailsDto.setDescription("Compra de laptop");
        detailsDto.setProductDto(productDto);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(detailsDto);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setUsername("usuario123");
        userDto.setEmail("usuario@example.com");

        InvoicesDto invoiceDto = new InvoicesDto();
        invoiceDto.setDetailsInvoices(detailsSet);
        invoiceDto.setUser(userDto);

        // Crear invoice
        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("usuario@example.com"));

        // Recuperar todos los invoices
        mockMvc.perform(get("/invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.email").value("usuario@example.com"));
    }

    @Test
    void testCreateInvoiceWithMultipleDetails() throws Exception {
        // Crear segundo producto
        Product product2 = new Product();
        product2.setIdProduct(20L);
        product2.setName("Mouse");
        product2.setStock(50);
        product2.setDescription("Mouse inalámbrico");
        product2.setPrice(java.math.BigDecimal.valueOf(250000));
        productRepository.save(product2);

        // Crear detalles con múltiples productos
        ProductDto productDto1 = new ProductDto();
        productDto1.setIdProduct(testProduct.getIdProduct());

        ProductDto productDto2 = new ProductDto();
        productDto2.setIdProduct(product2.getIdProduct());

        DetailsInvoiceDto details1 = new DetailsInvoiceDto();
        details1.setPrice(50000L);
        details1.setDescription("Compra de laptop");
        details1.setProductDto(productDto1);

        DetailsInvoiceDto details2 = new DetailsInvoiceDto();
        details2.setPrice(25000L);
        details2.setDescription("Compra de mouse");
        details2.setProductDto(productDto2);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(details1);
        detailsSet.add(details2);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setUsername("usuario123");
        userDto.setEmail("usuario@example.com");

        InvoicesDto invoiceDto = new InvoicesDto();
        invoiceDto.setDetailsInvoices(detailsSet);
        invoiceDto.setUser(userDto);

        // Crear invoice con múltiples productos
        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detailsInvoices.length()").value(2));
    }

    @Test
    void testInvoiceWithNonExistentUser() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(testProduct.getIdProduct());

        DetailsInvoiceDto detailsDto = new DetailsInvoiceDto();
        detailsDto.setPrice(50000L);
        detailsDto.setDescription("Compra de laptop");
        detailsDto.setProductDto(productDto);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(detailsDto);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setUsername("usuarioNoExiste");
        userDto.setEmail("noexiste@example.com");

        InvoicesDto invoiceDto = new InvoicesDto();
        invoiceDto.setDetailsInvoices(detailsSet);
        invoiceDto.setUser(userDto);

        // Intentar crear invoice con usuario no existente
        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvoiceWithNonExistentProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(999L); // ID de producto que no existe

        DetailsInvoiceDto detailsDto = new DetailsInvoiceDto();
        detailsDto.setPrice(50000L);
        detailsDto.setDescription("Compra de producto no existente");
        detailsDto.setProductDto(productDto);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(detailsDto);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setUsername("usuario123");
        userDto.setEmail("usuario@example.com");

        InvoicesDto invoiceDto = new InvoicesDto();
        invoiceDto.setDetailsInvoices(detailsSet);
        invoiceDto.setUser(userDto);

        // Intentar crear invoice con producto no existente
        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceDto)))
                .andExpect(status().isNotFound());
    }
}
