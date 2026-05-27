package com.sistema.productos.sistema_productos_jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank(message = "{user.username.notblank}")
    @Size(min = 8, max = 50, message = "{user.username.size}")
    private String username;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{user.email.pattern}")
    @Size(min = 8, max = 50, message = "{user.email.size}")
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_*])(?=\\S+$).{8,100}$", message = "{user.password.pattern}")
    private String password;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Boolean admin = false;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Boolean enable = true;

}
