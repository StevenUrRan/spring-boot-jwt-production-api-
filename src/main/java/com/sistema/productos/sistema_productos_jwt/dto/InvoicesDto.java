package com.sistema.productos.sistema_productos_jwt.dto;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicesDto {

    private Long id;

    @NotNull(message = "{invoice.details.notnull}")
    @Size(min = 1, message = "{invoice.details.size}")
    @Valid
    private Set<DetailsInvoiceDto> detailsInvoices;

    @NotNull(message = "{invoice.user.notnull}")
    @Valid
    private UserResponseDto user;
}
