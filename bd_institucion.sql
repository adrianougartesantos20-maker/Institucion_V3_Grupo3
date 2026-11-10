-- BD_INSTITUCION - Grupo 3

CREATE DATABASE IF NOT EXISTS BD_INSTITUCION CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE BD_INSTITUCION;

DROP TABLE IF EXISTS horarios, notas, asignaciones, cursos, asistencias, matriculas, usuarios, roles;

CREATE TABLE roles (
  id     INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO roles (id, nombre) VALUES
 (1, 'Administrador'),
 (2, 'Auxiliar'),
 (3, 'Docente'),
 (4, 'Alumno');

CREATE TABLE usuarios (
  id        INT PRIMARY KEY AUTO_INCREMENT,
  usuario   VARCHAR(20)  NOT NULL UNIQUE,
  clave     VARCHAR(255) NOT NULL,
  nombres   VARCHAR(60)  NOT NULL,
  apellidos VARCHAR(60)  NOT NULL,
  correo    VARCHAR(80)  DEFAULT '',
  telefono  VARCHAR(15)  DEFAULT '',
  id_rol    INT NOT NULL,
  token     VARCHAR(64)  DEFAULT NULL,
  FOREIGN KEY (id_rol) REFERENCES roles(id)
) ENGINE=InnoDB;

INSERT INTO usuarios (usuario, clave, nombres, apellidos, correo, telefono, id_rol) VALUES
 ('admin',    '$2y$10$D6MDj.pQDetiadBjHrBwj.S0vdDmMpG0V9X6YO.yp4fdSjRlqFUS2', 'Administrador', 'General', 'admin@institucion.edu.pe', '900000001', 1),
 ('auxiliar1','$2y$10$12rflaRQhy9sX6CV6SF1HemMEUG5TxdwZLHYmcMXMI2LZIKXePB3K', 'Rosa',  'Prueba Auxiliar', 'auxiliar@institucion.edu.pe', '900000002', 2),
 ('docente1', '$2y$10$12rflaRQhy9sX6CV6SF1HemMEUG5TxdwZLHYmcMXMI2LZIKXePB3K', 'Carlos','Prueba Docente',   'docente@institucion.edu.pe',  '900000003', 3),
 ('10000001', '$2y$10$12rflaRQhy9sX6CV6SF1HemMEUG5TxdwZLHYmcMXMI2LZIKXePB3K', 'Ana',   'Alumna Uno',       '', '', 4),
 ('10000002', '$2y$10$12rflaRQhy9sX6CV6SF1HemMEUG5TxdwZLHYmcMXMI2LZIKXePB3K', 'Luis',  'Alumno Dos',       '', '', 4);

CREATE TABLE matriculas (
  id        INT PRIMARY KEY AUTO_INCREMENT,
  id_alumno INT NOT NULL,
  grado     VARCHAR(10) NOT NULL,
  seccion   VARCHAR(5)  NOT NULL,
  periodo   VARCHAR(10) NOT NULL,
  UNIQUE KEY uk_alumno_periodo (id_alumno, periodo),
  FOREIGN KEY (id_alumno) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO matriculas (id_alumno, grado, seccion, periodo) VALUES
 (4, '1°', 'A', '2026'),
 (5, '1°', 'A', '2026');

CREATE TABLE asistencias (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  id_alumno   INT NOT NULL,
  fecha       DATE NOT NULL,
  hora        TIME NOT NULL,
  estado      VARCHAR(15) NOT NULL,
  id_auxiliar INT NOT NULL,
  UNIQUE KEY uk_alumno_fecha (id_alumno, fecha),
  FOREIGN KEY (id_alumno)   REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (id_auxiliar) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cursos (
  id     INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO cursos (nombre) VALUES
 ('Matemática'), ('Comunicación'), ('Ciencia y Tecnología'),
 ('Personal Social'), ('Inglés'), ('Educación Física'), ('Arte y Cultura');

CREATE TABLE asignaciones (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  id_docente INT NOT NULL,
  id_curso   INT NOT NULL,
  grado      VARCHAR(10) NOT NULL,
  seccion    VARCHAR(5)  NOT NULL,
  periodo    VARCHAR(10) NOT NULL,
  UNIQUE KEY uk_curso_seccion (id_curso, grado, seccion, periodo),
  FOREIGN KEY (id_docente) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (id_curso)   REFERENCES cursos(id)
) ENGINE=InnoDB;

INSERT INTO asignaciones (id_docente, id_curso, grado, seccion, periodo) VALUES
 (3, 1, '1°', 'A', '2026'),
 (3, 2, '1°', 'A', '2026');

CREATE TABLE notas (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  id_asignacion INT NOT NULL,
  id_alumno     INT NOT NULL,
  evaluacion    VARCHAR(40) NOT NULL,
  nota          DECIMAL(4,2) NOT NULL,
  fecha         DATE NOT NULL,
  UNIQUE KEY uk_nota (id_asignacion, id_alumno, evaluacion),
  FOREIGN KEY (id_asignacion) REFERENCES asignaciones(id) ON DELETE CASCADE,
  FOREIGN KEY (id_alumno)     REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE horarios (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  id_asignacion INT NOT NULL,
  dia           VARCHAR(10) NOT NULL,
  hora_inicio   VARCHAR(5)  NOT NULL,
  hora_fin      VARCHAR(5)  NOT NULL,
  FOREIGN KEY (id_asignacion) REFERENCES asignaciones(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO horarios (id_asignacion, dia, hora_inicio, hora_fin) VALUES
 (1, 'Lunes',     '08:00', '09:30'),
 (2, 'Lunes',     '09:45', '11:15'),
 (1, 'Miércoles', '08:00', '09:30');

