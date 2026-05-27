package com.sistema.productos.sistema_productos_jwt.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.productos.sistema_productos_jwt.dto.LoginRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.LoginResponseDto;
import com.sistema.productos.sistema_productos_jwt.service.jwt.JwtServiceInterface;
import com.sistema.productos.sistema_productos_jwt.validation.ResponseBodyController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {

    private AuthenticationManager authenticationManager;
    private JwtServiceInterface jwtService;
    private ResponseBodyController validation;


    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation.validation(result);
        }

        Authentication authentication = (Authentication) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails user = (UserDetails) authentication
                .getPrincipal();

        String token = jwtService.generateToken(user);

        Map<String, Object> response = new HashMap<>();

        response.put("data: ", new LoginResponseDto(token));
        response.put("message: ", "Inicio de sesion exitoso...");
        return ResponseEntity.ok(response);
    }
}
