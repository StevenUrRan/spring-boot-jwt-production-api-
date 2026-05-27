package com.sistema.productos.sistema_productos_jwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "{login.password.notblank}")
    @Size(min = 8, max = 50, message = "{login.password.size}")
    private String password;

    @NotBlank(message = "{login.email.notblank}")
    @Email(message = "{login.email.invalid}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{login.email.pattern}")
    @Size(min = 8, max = 50, message = "{login.email.size}")
    private String email;

}
