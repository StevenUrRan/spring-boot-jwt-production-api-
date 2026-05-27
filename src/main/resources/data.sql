INSERT INTO roles (name) VALUES ("ROLE_ADMIN");

INSERT INTO roles (name) VALUES ("ROLE_USER");

-- Inserciones para la tabla de Usuarios (las contraseñas idealmente deberían ir encriptadas si usas Spring Security)
INSERT INTO users (username, email, password, admin) VALUES ('steven_admin', 'steven.admin@sistema.com', 'SecurePass2026*Uniq','true');
INSERT INTO users (username, email, password) VALUES ('dev_trainee', 'desarrollo.trainee@productos.co', 'JavaSpring%Boot3.5');
INSERT INTO users (username, email, password) VALUES ('carlos_gomez', 'carlos.gomez99@gmail.com', 'Password99Mdf!');
INSERT INTO users (username, email, password) VALUES ('support_tech', 'soporte.tecnico@sistema-jwt.net', 'TechSupport#2026_Key');
INSERT INTO users (username, email, password) VALUES ('invitado_analista', 'analista.invitado@outlook.com', 'GuestAccess_2026*QA');

-- Inserciones para la tabla de Productos
INSERT INTO products (id_product, name, description, price, stock) 
VALUES (101, 'Teclado Mecánico Custom 75%', 'Teclado inalámbrico con switches lineales e iluminación RGB configurable.', 189990.00, 14);

INSERT INTO products (id_product, name, description, price, stock) 
VALUES (102, 'Memoria RAM DDR5 32GB', 'Kit de dos módulos de 16GB optimizados para alto rendimiento en desarrollo y gaming.', 135500.00, 25);

INSERT INTO products (id_product, name, description, price, stock) 
VALUES (103, 'Disco Sólido SSD NVMe 2TB', 'Unidad de almacenamiento de alta velocidad PCIe 4.0 con disipador térmico.', 160000.00, 18);

INSERT INTO products (id_product, name, description, price, stock) 
VALUES (104, 'Mouse Ergonómico Vertical', 'Mouse inalámbrico diseñado para reducir la fatiga muscular durante largas jornadas de trabajo.', 79950.00, 9);

INSERT INTO products (id_product, name, description, price, stock) 
VALUES (105, 'Router Wi-Fi 6 de Doble Banda', 'Router de alta velocidad con puertos Gigabit y cobertura expandida para múltiples dispositivos.', 210000.00, 7);