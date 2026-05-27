package com.sistema.productos.sistema_productos_jwt.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Long id;

    @NotNull(message = "{product.id.notnull}")
    @Min(value = 10, message = "{product.id.min}")
    @Max(value = 1000, message = "{product.id.max}")
    private Long idProduct;

    @NotBlank(message = "{product.name.notblank}")
    @Size(min = 8, max = 50, message = "{product.name.size}")
    private String name;

    @NotNull(message = "{product.stock.notnull}")
    @Range(min = 1, max = 100, message = "{product.stock.range}")
    private Integer stock;

    @NotBlank(message = "{product.description.notblank}")
    @Size(min = 8, max = 100, message = "{product.description.size}")
    private String description;

    @NotNull(message = "{product.price.notnull}")
    @DecimalMin(value = "1000.00", message = "{product.price.min}")
    @DecimalMax(value = "1000000.00", message = "{product.price.max}")
    @Digits(integer = 6, fraction = 2, message = "{product.price.digits}")
    private BigDecimal price;
}
