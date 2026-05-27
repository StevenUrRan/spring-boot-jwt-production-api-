package com.sistema.productos.sistema_productos_jwt.service.jwt;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.sistema_productos_jwt.entity.User;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private UserRepository userRepository;

        @Override
        @Transactional(readOnly = true)
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

                User userInstance = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado..."));

                List<SimpleGrantedAuthority> authorities = userInstance.getRoles()
                                .stream().map(rol -> new SimpleGrantedAuthority(rol.getName())).toList();

                return org.springframework.security.core.userdetails.User.builder()
                                .username(userInstance.getEmail())
                                .password(userInstance.getPassword())
                                .authorities(authorities)
                                .build();
        }

}
