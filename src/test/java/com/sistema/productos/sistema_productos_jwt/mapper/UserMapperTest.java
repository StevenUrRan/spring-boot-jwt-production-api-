package com.sistema.productos.sistema_productos_jwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.entity.User;

class UserMapperTest {

    private UserMapper userMapper;
    private User user;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        user = new User();
        user.setId(1L);
        user.setUsername("usuario123");
        user.setEmail("usuario@example.com");
        user.setPassword("hashedPassword");
        user.setEnable(true);
        user.setAdmin(false);

        userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("usuario123");
        userRequestDto.setEmail("usuario@example.com");
        userRequestDto.setPassword("password123");
        userRequestDto.setEnable(true);
        userRequestDto.setAdmin(false);
    }

    @Test
    void testToEntity_Success() {
        User result = userMapper.toEntity(userRequestDto);

        assertNotNull(result);
        assertEquals(userRequestDto.getUsername(), result.getUsername());
        assertEquals(userRequestDto.getEmail(), result.getEmail());
        assertEquals(userRequestDto.getPassword(), result.getPassword());
        assertEquals(userRequestDto.getEnable(), result.isEnable());
        assertEquals(userRequestDto.getAdmin(), result.isAdmin());
    }

    @Test
    void testToEntity_Null() {
        RuntimeException exception = null;
        try {
            userMapper.toEntity(null);
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    void testToDto_Success() {
        UserResponseDto result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testToDto_Null() {
        UserResponseDto result = userMapper.toDto(null);

        assertNull(result);
    }
}
