package com.sistema.productos.sistema_productos_jwt.controller;

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

import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.service.UserService;
import com.sistema.productos.sistema_productos_jwt.validation.ResponseBodyController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios: registro, consulta y administración")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private UserService userService;
    private ResponseBodyController validation;

    @Operation(summary = "Listar todos los usuarios",
               description = "Retorna la lista de todos los usuarios registrados. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios retornada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @GetMapping({"", "/list"})
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Buscar usuario por email",
               description = "Retorna los datos de un usuario identificado por su dirección de correo. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{email}")
    public ResponseEntity<?> findByEmail(
            @Parameter(description = "Correo electrónico del usuario", example = "admin@correo.com", required = true)
            @PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEmail(email));
    }

    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea una cuenta de usuario con contraseña cifrada. Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o usuario ya existente")
    })
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UserRequestDto request, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.newUser(request));
    }

    @Operation(summary = "Actualizar usuario",
               description = "Modifica los datos de un usuario existente. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{email}")
    public ResponseEntity<?> update(@Valid @RequestBody UserRequestDto request, BindingResult result,
            @Parameter(description = "Correo electrónico del usuario a actualizar", example = "admin@correo.com", required = true)
            @PathVariable String email) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(request, email));
    }

    @Operation(summary = "Eliminar usuario",
               description = "Elimina un usuario del sistema dado su correo electrónico. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o expirado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<?> delete(
            @Parameter(description = "Correo electrónico del usuario a eliminar", example = "admin@correo.com", required = true)
            @PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }
}
