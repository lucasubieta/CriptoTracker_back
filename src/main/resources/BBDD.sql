CREATE DATABASE cryptotracker;

USE cryptotracker;

CREATE TABLE IF NOT EXISTS `cryptotracker`.`usuario` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `contraseña` VARCHAR(255) NOT NULL,
  `correo` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4


CREATE TABLE IF NOT EXISTS `cryptotracker`.`config_notificacion` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `usuario_id` BIGINT(20) NULL DEFAULT NULL,
  `activo` TINYINT(1) NULL DEFAULT 0,
  `tipo` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `mensaje_personalizado` VARCHAR(255) NULL DEFAULT NULL,
  `umbral` DECIMAL(38,2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `config_notificacion_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `cryptotracker`.`usuario` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4


CREATE TABLE IF NOT EXISTS `cryptotracker`.`criptomoneda` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  `cantidad` DOUBLE NOT NULL,
  `billetera_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `billetera_id` (`billetera_id` ASC) VISIBLE,
  CONSTRAINT `criptomoneda_ibfk_1`
    FOREIGN KEY (`billetera_id`)
    REFERENCES `cryptotracker`.`billetera` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb4


CREATE TABLE IF NOT EXISTS `cryptotracker`.`billetera` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `usuario_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `billetera_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `cryptotracker`.`usuario` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4

