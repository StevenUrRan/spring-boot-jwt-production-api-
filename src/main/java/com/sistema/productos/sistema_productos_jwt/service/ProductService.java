package com.sistema.productos.sistema_productos_jwt.service;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDto> findAll(Pageable pageable);

    ProductDto findByName(String name);

    ProductDto newProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto, Long id);

    void deleteProduct(Long id);

}
