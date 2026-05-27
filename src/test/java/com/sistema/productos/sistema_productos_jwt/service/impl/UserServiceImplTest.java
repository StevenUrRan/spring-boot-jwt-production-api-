package com.sistema.productos.sistema_productos_jwt.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.entity.Role;
import com.sistema.productos.sistema_productos_jwt.entity.User;
import com.sistema.productos.sistema_productos_jwt.exception.EmailNotFoundException;
import com.sistema.productos.sistema_productos_jwt.exception.UserExistException;
import com.sistema.productos.sistema_productos_jwt.exception.UserNotExistException;
import com.sistema.productos.sistema_productos_jwt.mapper.UserMapper;
import com.sistema.productos.sistema_productos_jwt.repository.RoleRepository;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("usuario123");
        user.setEmail("usuario@example.com");
        user.setPassword("hashedPassword");
        user.setEnable(true);
        user.setAdmin(false);
        user.setRoles(new HashSet<>());

        userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("usuario123");
        userRequestDto.setEmail("usuario@example.com");
        userRequestDto.setPassword("password123");
        userRequestDto.setAdmin(false);
        userRequestDto.setEnable(true);

        userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("usuario123");
        userResponseDto.setEmail("usuario@example.com");

        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
    }

    @Test
    void testFindAll() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("usuario456");
        user2.setEmail("usuario2@example.com");

        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);
        when(userMapper.toDto(user2)).thenReturn(userResponseDto);

        List<UserResponseDto> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.findByEmail("usuario@example.com");

        assertNotNull(result);
        assertEquals("usuario@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("usuario@example.com");
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> {
            userService.findByEmail("noexiste@example.com");
        });

        verify(userRepository, times(1)).findByEmail("noexiste@example.com");
    }

    @Test
    void testNewUser_Success() {
        when(userRepository.existsByUsername("usuario123")).thenReturn(false);
        when(userRepository.existsByEmail("usuario@example.com")).thenReturn(false);
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.newUser(userRequestDto);

        assertNotNull(result);
        assertEquals("usuario123", result.getUsername());
        verify(userRepository, times(1)).existsByUsername("usuario123");
        verify(userRepository, times(1)).existsByEmail("usuario@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testNewUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("usuario123")).thenReturn(true);

        assertThrows(UserExistException.class, () -> {
            userService.newUser(userRequestDto);
        });

        verify(userRepository, times(1)).existsByUsername("usuario123");
    }

    @Test
    void testNewUser_EmailAlreadyExists() {
        when(userRepository.existsByUsername("usuario123")).thenReturn(false);
        when(userRepository.existsByEmail("usuario@example.com")).thenReturn(true);

        assertThrows(UserExistException.class, () -> {
            userService.newUser(userRequestDto);
        });

        verify(userRepository, times(1)).existsByUsername("usuario123");
        verify(userRepository, times(1)).existsByEmail("usuario@example.com");
    }

    @Test
    void testUpdateUser_Success() {
        UserRequestDto updateDto = new UserRequestDto();
        updateDto.setUsername("usuarioActualizado");
        updateDto.setEmail("usuarioActualizado@example.com");
        updateDto.setPassword("newPassword");

        when(userRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUser(updateDto, "usuario@example.com");

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail("usuario@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotExistException.class, () -> {
            userService.updateUser(userRequestDto, "noexiste@example.com");
        });

        verify(userRepository, times(1)).findByEmail("noexiste@example.com");
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findByEmail("usuario@example.com")).thenReturn(Optional.of(user));

        userService.deleteUser("usuario@example.com");

        verify(userRepository, times(1)).findByEmail("usuario@example.com");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotExistException.class, () -> {
            userService.deleteUser("noexiste@example.com");
        });

        verify(userRepository, times(1)).findByEmail("noexiste@example.com");
    }
}
