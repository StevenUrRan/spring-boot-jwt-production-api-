package com.sistema.productos.sistema_productos_jwt.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;

public interface InvoiceService {

    Page<InvoicesDto> findAll(Pageable pageable);

    InvoicesDto findById(Long id);

    InvoicesDto newInvoicesDto(InvoicesDto invoice);

    List<InvoicesDto> findByDateRange(LocalDateTime start, LocalDateTime end);
}
