package com.sistema.productos.sistema_productos_jwt.service;

import java.util.List;

import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;

public interface UserService {

    List<UserResponseDto> findAll();

    UserResponseDto findByEmail(String email);

    UserResponseDto newUser(UserRequestDto request);

    UserResponseDto updateUser(UserRequestDto request, String email);

    void deleteUser(String email);
}
