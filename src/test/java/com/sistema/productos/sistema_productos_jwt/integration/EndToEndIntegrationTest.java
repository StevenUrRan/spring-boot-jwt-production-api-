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
import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.repository.InvoiceRepository;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCompleteFlowCreateUserProductAndInvoice() throws Exception {
        // 1. Crear usuario
        UserRequestDto userDto = new UserRequestDto();
        userDto.setUsername("usuario123");
        userDto.setEmail("usuario@example.com");
        userDto.setPassword("Password123!");
        userDto.setAdmin(false);
        userDto.setEnable(true);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("usuario123"));

        // 2. Crear producto 1
        ProductDto productDto1 = new ProductDto();
        productDto1.setIdProduct(10L);
        productDto1.setName("Laptop HP");
        productDto1.setStock(10);
        productDto1.setDescription("Laptop HP 15 pulgadas");
        productDto1.setPrice(java.math.BigDecimal.valueOf(500000));

        var response1 = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto1)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse1 = response1.getResponse().getContentAsString();
        Long productId1 = objectMapper.readTree(jsonResponse1).get("id").asLong();

        // 3. Crear producto 2
        ProductDto productDto2 = new ProductDto();
        productDto2.setIdProduct(20L);
        productDto2.setName("Mouse Logitech");
        productDto2.setStock(50);
        productDto2.setDescription("Mouse inalámbrico");
        productDto2.setPrice(java.math.BigDecimal.valueOf(250000));

        var response2 = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto2)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse2 = response2.getResponse().getContentAsString();
        Long productId2 = objectMapper.readTree(jsonResponse2).get("id").asLong();

        // 4. Crear invoice con dos productos
        ProductDto assignedProduct1 = new ProductDto();
        assignedProduct1.setIdProduct(10L);

        ProductDto assignedProduct2 = new ProductDto();
        assignedProduct2.setIdProduct(20L);

        DetailsInvoiceDto details1 = new DetailsInvoiceDto();
        details1.setPrice(50000L);
        details1.setDescription("Compra de laptop");
        details1.setProductDto(assignedProduct1);

        DetailsInvoiceDto details2 = new DetailsInvoiceDto();
        details2.setPrice(25000L);
        details2.setDescription("Compra de mouse");
        details2.setProductDto(assignedProduct2);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(details1);
        detailsSet.add(details2);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("usuario123");
        userResponseDto.setEmail("usuario@example.com");

        InvoicesDto invoiceDto = new InvoicesDto();
        invoiceDto.setDetailsInvoices(detailsSet);
        invoiceDto.setUser(userResponseDto);

        var responseInvoice = mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("usuario@example.com"))
                .andExpect(jsonPath("$.detailsInvoices.length()").value(2))
                .andReturn();

        String invoiceResponse = responseInvoice.getResponse().getContentAsString();
        Long invoiceId = objectMapper.readTree(invoiceResponse).get("id").asLong();

        // 5. Recuperar invoice creado
        mockMvc.perform(get("/invoice/" + invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("usuario@example.com"))
                .andExpect(jsonPath("$.detailsInvoices.length()").value(2));

        // 6. Recuperar todos los invoices
        mockMvc.perform(get("/invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.email").value("usuario@example.com"));

        // 7. Verificar que el usuario fue creado
        mockMvc.perform(get("/user/usuario@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario123"));

        // 8. Verificar que los productos fueron creados
        mockMvc.perform(get("/product/Laptop HP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop HP"));

        mockMvc.perform(get("/product/Mouse Logitech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse Logitech"));
    }

    @Test
    void testMultipleUsersAndInvoices() throws Exception {
        // Crear dos usuarios
        UserRequestDto user1Dto = new UserRequestDto();
        user1Dto.setUsername("usuario1");
        user1Dto.setEmail("usuario1@example.com");
        user1Dto.setPassword("Password123!");
        user1Dto.setAdmin(false);
        user1Dto.setEnable(true);

        UserRequestDto user2Dto = new UserRequestDto();
        user2Dto.setUsername("usuario2");
        user2Dto.setEmail("usuario2@example.com");
        user2Dto.setPassword("Password123!");
        user2Dto.setAdmin(false);
        user2Dto.setEnable(true);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1Dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2Dto)))
                .andExpect(status().isCreated());

        // Crear producto
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(30L);
        productDto.setName("Teclado mecanico");
        productDto.setStock(20);
        productDto.setDescription("Teclado mecánico");
        productDto.setPrice(java.math.BigDecimal.valueOf(500000));

        var response = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        Long productId = objectMapper.readTree(jsonResponse).get("id").asLong();

        // Crear invoices para ambos usuarios
        ProductDto assignedProduct = new ProductDto();
        assignedProduct.setIdProduct(30L);

        DetailsInvoiceDto details = new DetailsInvoiceDto();
        details.setPrice(50000L);
        details.setDescription("Compra de teclado");
        details.setProductDto(assignedProduct);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(details);

        // Invoice para usuario 1
        UserResponseDto user1Response = new UserResponseDto();
        user1Response.setUsername("usuario1");
        user1Response.setEmail("usuario1@example.com");

        InvoicesDto invoice1Dto = new InvoicesDto();
        invoice1Dto.setDetailsInvoices(detailsSet);
        invoice1Dto.setUser(user1Response);

        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice1Dto)))
                .andExpect(status().isCreated());

        // Invoice para usuario 2
        UserResponseDto user2Response = new UserResponseDto();
        user2Response.setUsername("usuario2");
        user2Response.setEmail("usuario2@example.com");

        InvoicesDto invoice2Dto = new InvoicesDto();
        invoice2Dto.setDetailsInvoices(detailsSet);
        invoice2Dto.setUser(user2Response);

        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice2Dto)))
                .andExpect(status().isCreated());

        // Verificar que se crearon 2 invoices
        mockMvc.perform(get("/invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}
