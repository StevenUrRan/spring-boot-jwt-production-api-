package com.sistema.productos.sistema_productos_jwt.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.exception.InvoiceNotFoundException;
import com.sistema.productos.sistema_productos_jwt.service.InvoiceService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    private InvoicesDto invoicesDto;
    private UserResponseDto userResponseDto;
    private DetailsInvoiceDto detailsInvoiceDto;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("usuario123");
        userResponseDto.setEmail("usuario@example.com");

        productDto = new ProductDto();
        productDto.setIdProduct(1L);
        productDto.setName("Laptop");
        productDto.setPrice(java.math.BigDecimal.valueOf(1500000));
        productDto.setStock(10);
        productDto.setDescription("Laptop HP");

        detailsInvoiceDto = new DetailsInvoiceDto();
        detailsInvoiceDto.setPrice(50000L);
        detailsInvoiceDto.setDescription("Compra de laptop");
        detailsInvoiceDto.setProductDto(productDto);

        Set<DetailsInvoiceDto> detailsSet = new HashSet<>();
        detailsSet.add(detailsInvoiceDto);

        invoicesDto = new InvoicesDto();
        invoicesDto.setDetailsInvoices(detailsSet);
        invoicesDto.setUser(userResponseDto);
    }

    @Test
    void testFindAll() throws Exception {
        InvoicesDto invoice2 = new InvoicesDto();
        invoice2.setDetailsInvoices(new HashSet<>());
        invoice2.setUser(userResponseDto);

        List<InvoicesDto> invoices = Arrays.asList(invoicesDto, invoice2);
        org.springframework.data.domain.Page<InvoicesDto> page = new org.springframework.data.domain.PageImpl<>(invoices);
        when(invoiceService.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].user.email").value("usuario@example.com"));
    }

    @Test
    void testFindById_Success() throws Exception {
        when(invoiceService.findById(1L)).thenReturn(invoicesDto);

        mockMvc.perform(get("/invoice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("usuario@example.com"));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        when(invoiceService.findById(999L)).thenThrow(new InvoiceNotFoundException());

        mockMvc.perform(get("/invoice/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNewInvoice_Success() throws Exception {
        when(invoiceService.newInvoicesDto(any(InvoicesDto.class))).thenReturn(invoicesDto);

        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoicesDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("usuario@example.com"));
    }

    @Test
    void testNewInvoice_InvalidRequest() throws Exception {
        InvoicesDto invalidInvoice = new InvoicesDto();
        invalidInvoice.setDetailsInvoices(new HashSet<>());

        mockMvc.perform(post("/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInvoice)))
                .andExpect(status().isBadRequest());
    }
}
