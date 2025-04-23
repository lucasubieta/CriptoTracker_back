CREATE DATABASE cryptotracker;

USE cryptotracker;

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL
);

CREATE TABLE billetera (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE criptomoneda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    cantidad DOUBLE NOT NULL,
    billetera_id BIGINT,
    FOREIGN KEY (billetera_id) REFERENCES billetera(id)
);

CREATE TABLE config_notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    activo BOOLEAN DEFAULT FALSE,
    tipo VARCHAR(50),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
