package com.sistema.productos.sistema_productos_jwt.mapper;

import org.springframework.stereotype.Component;

import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto request) {
        if (request == null) {
            throw new RuntimeException("los valores no pueden ser validos");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setAdmin(request.getAdmin() != null ? request.getAdmin() : false);
        newUser.setEnable(request.getEnable() != null ? request.getEnable() : true);
        return newUser;
    }

    public UserResponseDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
