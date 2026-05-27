package com.sistema.productos.sistema_productos_jwt.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.entity.Invoice;

@Component
public class InvoiceMapper {

    private final DetailsInvoiceMapper detailsInvoiceMapper;
    private final UserMapper userMapper;

    public InvoiceMapper(DetailsInvoiceMapper detailsInvoiceMapper, UserMapper userMapper) {
        this.detailsInvoiceMapper = detailsInvoiceMapper;
        this.userMapper = userMapper;
    }

    public Invoice toEntity(InvoicesDto dto) {
        if (dto == null) {
            return null;
        }

        
        Invoice entity = new Invoice();

        Set<DetailsInvoiceDto> detailsDto = dto.getDetailsInvoices();
        if (detailsDto != null) {
            entity.setDetailsInvoices(detailsDto.stream()
                    .map(detailsInvoiceMapper::toEntity)
                    .collect(Collectors.toSet()));
        }
        return entity;
    }

    public InvoicesDto toDto(Invoice entity) {
        if (entity == null) {
            return null;
        }
        
        InvoicesDto dto = new InvoicesDto();
        dto.setId(entity.getId());
        if (entity.getDetailsInvoices() != null) {
            dto.setDetailsInvoices(entity.getDetailsInvoices().stream()
                    .map(detailsInvoiceMapper::toDto)
                    .collect(Collectors.toSet()));
        }
        dto.setUser(userMapper.toDto(entity.getUser()));
        return dto;
    }
}
