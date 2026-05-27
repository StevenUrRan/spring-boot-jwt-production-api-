package com.sistema.productos.sistema_productos_jwt.mapper;

import org.springframework.stereotype.Component;

import com.sistema.productos.sistema_productos_jwt.dto.DetailsInvoiceDto;
import com.sistema.productos.sistema_productos_jwt.entity.DetailsInvoice;
import com.sistema.productos.sistema_productos_jwt.exception.ProductNotFoundException;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;

@Component
public class DetailsInvoiceMapper {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public DetailsInvoiceMapper(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    public DetailsInvoice toEntity(DetailsInvoiceDto dto) {
        if (dto == null) {
            return null;
        }
        DetailsInvoice entity = new DetailsInvoice();
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());

        if (dto.getProductDto() != null && dto.getProductDto().getIdProduct() != null) {
            entity.setProduct(productRepository.findByIdProduct(dto.getProductDto().getIdProduct())
                    .orElseThrow(ProductNotFoundException::new));
        }
        return entity;
    }

    public DetailsInvoiceDto toDto(DetailsInvoice entity) {
        if (entity == null) {
            return null;
        }
        DetailsInvoiceDto dto = new DetailsInvoiceDto();
        dto.setPrice(entity.getPrice());
        dto.setDescription(entity.getDescription());
        dto.setProductDto(productMapper.toDto(entity.getProduct()));
        return dto;
    }
}
