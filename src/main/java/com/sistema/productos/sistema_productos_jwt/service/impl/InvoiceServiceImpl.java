package com.sistema.productos.sistema_productos_jwt.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.entity.Invoice;
import com.sistema.productos.sistema_productos_jwt.entity.User;
import com.sistema.productos.sistema_productos_jwt.exception.BusinessException;
import com.sistema.productos.sistema_productos_jwt.exception.InvoiceNotFoundException;
import com.sistema.productos.sistema_productos_jwt.exception.UserNotExistException;
import com.sistema.productos.sistema_productos_jwt.mapper.InvoiceMapper;
import com.sistema.productos.sistema_productos_jwt.repository.InvoiceRepository;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;
import com.sistema.productos.sistema_productos_jwt.service.InvoiceService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceRepository invoiceRepository;
    private InvoiceMapper invoiceMapper;
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<InvoicesDto> findAll(Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findAll(pageable);
        List<InvoicesDto> dtos = invoicePage.getContent().stream()
                .map(invoiceMapper::toDto)
                .toList();
        return new PageImpl<>(dtos, pageable, invoicePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoicesDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(InvoiceNotFoundException::new);
        return invoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional
    public InvoicesDto newInvoicesDto(InvoicesDto invoice) {
        if (invoice == null) {
            throw new BusinessException();
        }
        User user = userRepository.findByEmail(invoice.getUser().getEmail()).orElseThrow(UserNotExistException::new);
        Invoice newInvoice = invoiceMapper.toEntity(invoice);
        newInvoice.setUser(user);
        return invoiceMapper.toDto(invoiceRepository.save(newInvoice));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoicesDto> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return invoiceRepository.findByDateBetween(start, end).stream()
                .map(invoiceMapper::toDto)
                .toList();
    }
}
