package com.sistema.productos.sistema_productos_jwt.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.productos.sistema_productos_jwt.dto.ProductDto;
import com.sistema.productos.sistema_productos_jwt.service.ProductService;
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
@RequestMapping("/product")
@AllArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD sobre el catálogo de productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private ProductService productService;
    private ResponseBodyController validation;

    @Operation(summary = "Listar productos paginados",
               description = "Retorna una página de productos. Parámetros: page (0-based), size (default 20), sort (ej. name,asc).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de productos retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDto>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @Operation(summary = "Buscar producto por nombre",
               description = "Retorna el producto que coincida exactamente con el nombre indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{name}")
    public ResponseEntity<?> findByName(
            @Parameter(description = "Nombre exacto del producto", example = "Teclado mecánico", required = true)
            @PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findByName(name));
    }

    @Operation(summary = "Crear nuevo producto",
               description = "Registra un nuevo producto en el catálogo. Requiere rol ADMIN. El nombre debe ser único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre ya existente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PostMapping
    public ResponseEntity<?> newProduct(@Valid @RequestBody ProductDto productDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.newProduct(productDto));
    }

    @Operation(summary = "Actualizar producto",
               description = "Actualiza los datos de un producto existente dado su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductDto productDto, BindingResult result,
            @Parameter(description = "ID numérico del producto", example = "1", required = true)
            @PathVariable Long id) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productDto, id));
    }

    @Operation(summary = "Eliminar producto",
               description = "Elimina permanentemente un producto del catálogo dado su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID numérico del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
