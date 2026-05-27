package com.sistema.productos.sistema_productos_jwt.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.sistema.productos.sistema_productos_jwt.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException());
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto newUser(UserRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistException();
        }
        User user = userMapper.toEntity(request);
        Set<Role> roles = new HashSet<>();
        Optional<Role> rolesOptional = roleRepository.findByName("ROLE_USER");
        rolesOptional.ifPresent(roles::add);

        if (user.isAdmin()) {
            Optional<Role> adminOptional = roleRepository.findByName("ROLE_ADMIN");
            adminOptional.ifPresent(roles::add);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));

    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserRequestDto request, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotExistException());

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return userMapper.toDto(userRepository.save(user));

    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotExistException());
        userRepository.delete(user);
    }

}
