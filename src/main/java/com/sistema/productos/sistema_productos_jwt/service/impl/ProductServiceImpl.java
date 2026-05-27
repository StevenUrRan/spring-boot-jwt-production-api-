package com.sistema.productos.sistema_productos_jwt.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.entity.Product;
import com.sistema.productos.sistema_productos_jwt.exception.NameProductExistException;
import com.sistema.productos.sistema_productos_jwt.exception.ProductNotFoundException;
import com.sistema.productos.sistema_productos_jwt.mapper.ProductMapper;
import com.sistema.productos.sistema_productos_jwt.repository.ProductRepository;
import com.sistema.productos.sistema_productos_jwt.service.ProductService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductDto> dtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .toList();
        return new PageImpl<>(dtos, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(ProductNotFoundException::new);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto newProduct(ProductDto productDto) {
        if (productRepository.existsByName(productDto.getName())) {
            throw new NameProductExistException();
        }
        Product newProduct = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto, Long id) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setStock(productDto.getStock());
        product.setPrice(productDto.getPrice());
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
