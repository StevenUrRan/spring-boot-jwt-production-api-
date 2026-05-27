package com.sistema.productos.sistema_productos_jwt.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsInvoiceDto {

    @NotNull(message = "{details.price.notnull}")
    @Min(value = 1000, message = "{details.price.min}")
    @Max(value = 1000000, message = "{details.price.max}")
    private Long price;

    @NotBlank(message = "{details.description.notblank}")
    @Size(min = 8, max = 100, message = "{details.description.size}")
    private String description;

    private ProductDto productDto;
}
