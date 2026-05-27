package com.sistema.productos.sistema_productos_jwt.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.dto.UserResponseDto;
import com.sistema.productos.sistema_productos_jwt.exception.EmailNotFoundException;
import com.sistema.productos.sistema_productos_jwt.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("usuario123");
        userRequestDto.setEmail("usuario@example.com");
        userRequestDto.setPassword("Password123!");
        userRequestDto.setAdmin(false);
        userRequestDto.setEnable(true);

        userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("usuario123");
        userResponseDto.setEmail("usuario@example.com");
    }

    @Test
    void testFindAll() throws Exception {
        UserResponseDto user2 = new UserResponseDto();
        user2.setUsername("usuario456");
        user2.setEmail("usuario2@example.com");

        List<UserResponseDto> users = Arrays.asList(userResponseDto, user2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("usuario123"))
                .andExpect(jsonPath("$[1].username").value("usuario456"));
    }

    @Test
    void testFindByEmail_Success() throws Exception {
        when(userService.findByEmail("usuario@example.com")).thenReturn(userResponseDto);

        mockMvc.perform(get("/user/usuario@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario123"))
                .andExpect(jsonPath("$.email").value("usuario@example.com"));
    }

    @Test
    void testFindByEmail_NotFound() throws Exception {
        when(userService.findByEmail("noexiste@example.com")).thenThrow(new EmailNotFoundException());

        mockMvc.perform(get("/user/noexiste@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNewUser_Success() throws Exception {
        when(userService.newUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("usuario123"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(any(UserRequestDto.class), anyString())).thenReturn(userResponseDto);

        mockMvc.perform(put("/user/usuario@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario123"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser("usuario@example.com");

        mockMvc.perform(delete("/user/usuario@example.com"))
                .andExpect(status().isOk());
    }
}
