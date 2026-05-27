package com.sistema.productos.sistema_productos_jwt.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.entity.DetailsInvoice;
import com.sistema.productos.sistema_productos_jwt.entity.Invoice;
import com.sistema.productos.sistema_productos_jwt.entity.Product;
import com.sistema.productos.sistema_productos_jwt.entity.User;
import com.sistema.productos.sistema_productos_jwt.exception.BusinessException;
import com.sistema.productos.sistema_productos_jwt.exception.InvoiceNotFoundException;
import com.sistema.productos.sistema_productos_jwt.exception.UserNotExistException;
import com.sistema.productos.sistema_productos_jwt.mapper.InvoiceMapper;
import com.sistema.productos.sistema_productos_jwt.repository.DetailsInvoiceRepository;
import com.sistema.productos.sistema_productos_jwt.repository.InvoiceRepository;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

class InvoiceServceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DetailsInvoiceRepository detailsInvoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice invoice;
    private InvoicesDto invoicesDto;
    private User user;
    private UserResponseDto userResponseDto;
    private Product product;
    private DetailsInvoice detailsInvoice;
    private DetailsInvoiceDto detailsInvoiceDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("usuario123");
        user.setEmail("usuario@example.com");

        userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("usuario123");
        userResponseDto.setEmail("usuario@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(java.math.BigDecimal.valueOf(1500000));
        product.setStock(10);
        product.setDescription("Laptop HP");

        detailsInvoice = new DetailsInvoice();
        detailsInvoice.setId(1L);
        detailsInvoice.setPrice(50000L);
        detailsInvoice.setDescription("Compra de laptop");
        detailsInvoice.setProduct(product);

        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(100L);
        productDto.setName("Laptop");

        detailsInvoiceDto = new DetailsInvoiceDto();
        detailsInvoiceDto.setPrice(50000L);
        detailsInvoiceDto.setDescription("Compra de laptop");
        detailsInvoiceDto.setProductDto(productDto);

        Set<DetailsInvoice> detailsSet = new HashSet<>();
        detailsSet.add(detailsInvoice);

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setUser(user);
        invoice.setDetailsInvoices(detailsSet);

        Set<DetailsInvoiceDto> detailsDtoSet = new HashSet<>();
        detailsDtoSet.add(detailsInvoiceDto);

        invoicesDto = new InvoicesDto();
        invoicesDto.setDetailsInvoices(detailsDtoSet);
        invoicesDto.setUser(userResponseDto);
    }

    @Test
    void testFindAll() {
        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);

        List<Invoice> invoices = Arrays.asList(invoice, invoice2);
        org.springframework.data.domain.Page<Invoice> page = new org.springframework.data.domain.PageImpl<>(invoices);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        
        when(invoiceRepository.findAll(pageable)).thenReturn(page);
        when(invoiceMapper.toDto(invoice)).thenReturn(invoicesDto);
        when(invoiceMapper.toDto(invoice2)).thenReturn(invoicesDto);

        org.springframework.data.domain.Page<InvoicesDto> result = invoiceService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        verify(invoiceRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toDto(invoice)).thenReturn(invoicesDto);

        InvoicesDto result = invoiceService.findById(1L);

        assertNotNull(result);
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> {
            invoiceService.findById(999L);
        });

        verify(invoiceRepository, times(1)).findById(999L);
    }

    @Test
    void testNewInvoicesDto_Success() {
        when(userRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(user));
        when(invoiceMapper.toEntity(invoicesDto)).thenReturn(invoice);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toDto(invoice)).thenReturn(invoicesDto);

        InvoicesDto result = invoiceService.newInvoicesDto(invoicesDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail("usuario@example.com");
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void testNewInvoicesDto_UserNotFound() {
        when(userRepository.findByEmail("usuario@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotExistException.class, () -> {
            invoiceService.newInvoicesDto(invoicesDto);
        });

        verify(userRepository, times(1)).findByEmail("usuario@example.com");
    }

    @Test
    void testNewInvoicesDto_NullInvoice() {
        assertThrows(BusinessException.class, () -> {
            invoiceService.newInvoicesDto(null);
        });
    }
}
