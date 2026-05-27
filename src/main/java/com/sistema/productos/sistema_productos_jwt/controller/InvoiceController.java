package com.sistema.productos.sistema_productos_jwt.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.productos.sistema_productos_jwt.dto.InvoicesDto;
import com.sistema.productos.sistema_productos_jwt.service.InvoiceService;
import com.sistema.productos.sistema_productos_jwt.validation.ResponseBodyController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/invoice")
@AllArgsConstructor
@Tag(name = "Facturas", description = "Operaciones sobre facturas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private InvoiceService invoiceService;
    private ResponseBodyController validation;

    @Operation(summary = "Listar facturas paginadas",
               description = "Retorna una página de facturas. Soporta los parámetros estándar de paginación: page, size y sort.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de facturas retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @GetMapping
    public ResponseEntity<Page<InvoicesDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(invoiceService.findAll(pageable));
    }

    @Operation(summary = "Buscar factura por ID",
               description = "Retorna una factura específica dado su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
            @Parameter(description = "ID numérico de la factura", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(invoiceService.findById(id));
    }

    @Operation(summary = "Buscar facturas por rango de fechas",
               description = "Retorna todas las facturas cuya fecha de creación esté dentro del rango indicado. "
                       + "Formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facturas en el rango"),
            @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado")
    })
    @GetMapping("/search")
    public ResponseEntity<List<InvoicesDto>> searchByDateRange(
            @Parameter(description = "Fecha de inicio (ISO-8601)", example = "2024-01-01T00:00:00", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Fecha de fin (ISO-8601)", example = "2024-12-31T23:59:59", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(invoiceService.findByDateRange(start, end));
    }

    @Operation(summary = "Crear nueva factura",
               description = "Registra una nueva factura asociada a un usuario existente en el sistema. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Factura creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la factura inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> newInvoice(@Valid @RequestBody InvoicesDto invoicesDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.newInvoicesDto(invoicesDto));
    }
}
