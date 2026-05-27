# Sistema Productos JWT

API REST modernizada y profesionalizada para gestionar usuarios, productos y facturas con autenticación basada en JWT. El proyecto cuenta con documentación OpenAPI/Swagger interactiva, perfiles de configuración separados, paginación, búsquedas avanzadas personalizadas y contenedorización completa en Docker.

---

## 🛠️ Tecnologías y Versiones

- **Java 17** (Eclipse Temurin)
- **Spring Boot 3.5.14**
- **Spring Web / Spring Security**
- **Spring Data JPA**
- **Spring Validation** (Bean Validation con respuestas estandarizadas)
- **PostgreSQL 16** (Base de datos principal)
- **H2 Database** (Utilizada en perfil `test` para pruebas automatizadas)
- **JWT** con la librería `jjwt` (versión `0.13.0`)
- **Lombok**
- **Maven Wrapper**
- **Docker / Docker Compose**

---

## ✨ Características Profesionales Implementadas

1. **Documentación Swagger/OpenAPI Completa:**
   - Disponible en local en: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Todos los endpoints documentados con operaciones (`@Operation`), respuestas detalladas (`@ApiResponse`) y parámetros interactivos.
   - Seguridad integrada con soporte para tokens Bearer (puedes probar directamente las llamadas seguras desde la UI).

2. **Perfiles de Configuración Separados:**
   - **`dev` (Desarrollo - Activo por defecto):** Utiliza base de datos PostgreSQL local (`localhost:5432/jwt_productos`) con persistencia automática (`ddl-auto=update`), formateo y salida de logs SQL (`show-sql=true`).
   - **`prod` (Producción):** Activable mediante `-Dspring.profiles.active=prod`. Requiere estrictamente variables de entorno para las credenciales y oculta la salida de sentencias SQL por seguridad.
   - **`test` (Pruebas):** Configurado para ejecutar pruebas utilizando base de datos en memoria H2.

3. **Paginación en Endpoints de Listado:**
   - Los endpoints `GET /product` y `GET /invoice` devuelven la información paginada en la estructura estándar de Spring Data (`Page<T>`), evitando la sobrecarga de memoria del servidor.
   - Soporta parámetros como: `?page=0&size=10&sort=name,asc`.

4. **Query Personalizada (JPQL):**
   - Búsqueda avanzada de facturas en el repositorio mediante `@Query`:
     `GET /invoice/search?start=2026-01-01T00:00:00&end=2026-12-31T23:59:59`
   - Retorna todas las facturas en el rango de fecha de creación especificado, ordenadas de forma descendente.

5. **Manejo Seguro de Excepciones y JWT Expirados:**
   - Se implementó un flujo que intercepta tokens expirados o inválidos en `JwtAuthenticationFilter`.
   - Si el token está expirado, retorna un código de estado `401 Unauthorized` de inmediato con un JSON estructurado de error (`TOKEN_EXPIRED` o `TOKEN_INVALID`), evitando respuestas HTML por defecto de Spring Security.
   - Centralizado a través de `@RestControllerAdvice` en `GlobalHandlerException`.

6. **Hardening en Docker (No-Root):**
   - El `Dockerfile` utiliza compilación multi-stage y crea un usuario y grupo no privilegios (`appuser:appgroup`) para la ejecución segura del JRE.

---

## 🔑 Variables de Entorno

La aplicación lee las siguientes variables de entorno para mayor seguridad en producción:

| Variable | Descripción | Valor por Defecto (`dev`) |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Perfil de configuración a usar | `dev` |
| `DB_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `1234` |
| `JWT_KEY` | Llave secreta para firma de tokens JWT | Llave segura por defecto local |

---

## 🚀 Instrucciones de Ejecución

### 1. Ejecutar de forma Local con Maven

Asegúrate de tener corriendo PostgreSQL localmente y haber creado la base de datos `jwt_productos`:
```sql
CREATE DATABASE jwt_productos;
```

Exporta las variables de entorno de tu base de datos y corre el proyecto:

*   **Linux/macOS:**
    ```bash
    export DB_USERNAME=tu_usuario
    export DB_PASSWORD=tu_password
    export JWT_KEY=una_llave_secreta_de_al_menos_256_bits
    ./mvnw spring-boot:run
    ```

*   **Windows PowerShell:**
    ```powershell
    $env:DB_USERNAME="tu_usuario"
    $env:DB_PASSWORD="tu_password"
    $env:JWT_KEY="una_llave_secreta_de_al_menos_256_bits"
    .\mvnw.cmd spring-boot:run
    ```

### 2. Ejecutar con Docker Compose (Recomendado 🐋)

No necesitas instalar ni configurar PostgreSQL en tu máquina local. Con un solo comando levantarás la aplicación y la base de datos juntas:
```bash
docker compose up --build
```
*Nota: La base de datos persistirá la información en un volumen local `postgres_data`.*

---

## 📑 Endpoints Principales

### Autenticación (Público)

- `POST /login`: Retorna el token JWT.
  ```json
  {
    "email": "usuario@correo.com",
    "password": "Password123!"
  }
  ```

### Usuarios (Requiere Rol `ADMIN`)

- `POST /user`: Registra un nuevo usuario (Público).
- `GET /user/list` o `GET /user`: Obtiene el listado completo.
- `GET /user/{email}`: Detalle de usuario.
- `PUT /user/{email}`: Actualiza los datos de un usuario.
- `DELETE /user/{email}`: Elimina un usuario.

### Productos (Autenticado - Roles `ADMIN` / `USER`)

- `GET /product`: Retorna una lista **paginada** de productos.
- `GET /product/{name}`: Detalle de un producto por nombre.
- `POST /product` (Solo `ADMIN`): Registra un producto.
- `PUT /product/{id}` (Solo `ADMIN`): Modifica datos de un producto.
- `DELETE /product/{id}` (Solo `ADMIN`): Elimina un producto.

### Facturas (Autenticado - Roles `ADMIN` / `USER`)

- `GET /invoice`: Retorna una lista **paginada** de facturas.
- `GET /invoice/{id}`: Detalle de factura por ID.
- `GET /invoice/search?start=...&end=...`: Busca facturas por rango de fechas (JPQL).
- `POST /invoice` (Solo `ADMIN`): Crea una nueva factura.

---

## 🧪 Pruebas Automatizadas

Para validar que todo compile y los tests (incluidos los de integración End-to-End) pasen correctamente usando la base de datos H2:
```bash
./mvnw clean test
```
