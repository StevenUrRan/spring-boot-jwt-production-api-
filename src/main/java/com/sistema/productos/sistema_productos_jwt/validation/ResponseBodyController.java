package com.sistema.productos.sistema_productos_jwt.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class ResponseBodyController {

    public ResponseEntity<Map<String, Object>> validation(BindingResult result) {
        Map<String, Object> erros = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            erros.put(err.getField(), " " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(erros);
    }

}
