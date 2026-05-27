package com.sistema.productos.sistema_productos_jwt.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.sistema_productos_jwt.dto.UserRequestDto;
import com.sistema.productos.sistema_productos_jwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateAndRetrieveUser() throws Exception {
        UserRequestDto userDto = new UserRequestDto();
        userDto.setUsername("usuario123");
        userDto.setEmail("usuario@example.com");
        userDto.setPassword("Password123!");
        userDto.setAdmin(false);
        userDto.setEnable(true);

        // Crear usuario
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("usuario123"));

        // Recuperar todos los usuarios
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("usuario123"));

        // Buscar por email
        mockMvc.perform(get("/user/usuario@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("usuario@example.com"));
    }

    @Test
    void testCreateUpdateAndDeleteUser() throws Exception {
        UserRequestDto userDto = new UserRequestDto();
        userDto.setUsername("usuario456");
        userDto.setEmail("usuario456@example.com");
        userDto.setPassword("Password123!");
        userDto.setAdmin(false);
        userDto.setEnable(true);

        // Crear usuario
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        // Actualizar usuario
        UserRequestDto updatedDto = new UserRequestDto();
        updatedDto.setUsername("usuarioActualizado");
        updatedDto.setEmail("usuarioActualizado@example.com");
        updatedDto.setPassword("Newpassword123!");

        mockMvc.perform(put("/user/usuario456@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        // Eliminar usuario
        mockMvc.perform(delete("/user/usuarioActualizado@example.com"))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/user/usuarioActualizado@example.com"))
                .andExpect(status().isNotFound());
    }
}
