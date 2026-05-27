package com.sistema.productos.sistema_productos_jwt.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> bussinesException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "Error en el sistema, por favor vuelva a intentarlo...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> emailNotFoundException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "EL email ingresado no existe en el sistema por favor ingresar un dato valido...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<?> invoiceNotFoundException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "La factura con dicho atributo no existe...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(NameProductExistException.class)
    public ResponseEntity<?> nameProductExistException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "El producto con dicho nombre no existe  en el sistema...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> ProductNotFoundException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "Producto no encontrado en el sistema...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<?> userExistException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "Usuario existente en el sistema, por favor volver a intentarlo...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<?> userNotExistException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "El usuario no existe en el sistema, por favor volver a intentarlo...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Error: ", "Credenciales incorrectas, por favor volver a intentarlo...");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(RuntimeException e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Error: ", "Error en la estructura del Json...");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalException(Exception e) {
        Map<String, Object> errors = new HashMap<>();

        errors.put("Message: ", "Ocurrio un error inesperado en el sistema...");
        errors.put("Causa: ", e.getMessage());
        errors.put("Date: ", new Date());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
