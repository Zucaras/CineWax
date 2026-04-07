-- ============================================================
-- CineWax - data.sql
-- Datos semilla para pruebas de todos los flujos
-- Coloca este archivo en: src/main/resources/data.sql
-- Spring Boot lo ejecuta automaticamente al arrancar
-- ============================================================
-- NOTA: Asegurate de tener en application.properties:
--   spring.jpa.hibernate.ddl-auto=create
--   spring.sql.init.mode=always
--   spring.jpa.defer-datasource-initialization=true
-- ============================================================

-- ══════════════════════════════════════════════════════════════
--  1. ESTADOS (5 estados - IDs deben coincidir con FloydService)
-- ══════════════════════════════════════════════════════════════
INSERT INTO estado (id_estado, nombre_estado) VALUES ('J11', 'Jalisco');
INSERT INTO estado (id_estado, nombre_estado) VALUES ('N11', 'Nuevo Leon');
INSERT INTO estado (id_estado, nombre_estado) VALUES ('E11', 'Estado de Mexico');
INSERT INTO estado (id_estado, nombre_estado) VALUES ('C11', 'Ciudad de Mexico');
INSERT INTO estado (id_estado, nombre_estado) VALUES ('S11', 'Sonora');

-- ══════════════════════════════════════════════════════════════
--  2. MUNICIPIOS (~9 por estado = 45 municipios)
-- ══════════════════════════════════════════════════════════════

-- ---- Jalisco (J11) ----
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11A', 'J11', 'A', 'Guadalajara');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11B', 'J11', 'B', 'Zapopan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11C', 'J11', 'C', 'Tlaquepaque');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11D', 'J11', 'D', 'Tonala');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11E', 'J11', 'E', 'Tlajomulco');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11F', 'J11', 'F', 'Puerto Vallarta');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11G', 'J11', 'G', 'Lagos de Moreno');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11H', 'J11', 'H', 'Tepatitlan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('J11I', 'J11', 'I', 'Chapala');

-- ---- Nuevo Leon (N11) ----
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11A', 'N11', 'A', 'Monterrey');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11B', 'N11', 'B', 'San Pedro Garza Garcia');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11C', 'N11', 'C', 'San Nicolas de los Garza');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11D', 'N11', 'D', 'Apodaca');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11E', 'N11', 'E', 'Guadalupe');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11F', 'N11', 'F', 'Santa Catarina');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11G', 'N11', 'G', 'Escobedo');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11H', 'N11', 'H', 'Juarez');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('N11I', 'N11', 'I', 'Garcia');

-- ---- Estado de Mexico (E11) ----
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11A', 'E11', 'A', 'Toluca');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11B', 'E11', 'B', 'Naucalpan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11C', 'E11', 'C', 'Ecatepec');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11D', 'E11', 'D', 'Tlalnepantla');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11E', 'E11', 'E', 'Atizapan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11F', 'E11', 'F', 'Metepec');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11G', 'E11', 'G', 'Huixquilucan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11H', 'E11', 'H', 'Nezahualcoyotl');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('E11I', 'E11', 'I', 'Texcoco');

-- ---- Ciudad de Mexico (C11) ----
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11A', 'C11', 'A', 'Cuauhtemoc');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11B', 'C11', 'B', 'Benito Juarez');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11C', 'C11', 'C', 'Miguel Hidalgo');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11D', 'C11', 'D', 'Coyoacan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11E', 'C11', 'E', 'Alvaro Obregon');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11F', 'C11', 'F', 'Tlalpan');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11G', 'C11', 'G', 'Iztapalapa');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11H', 'C11', 'H', 'Azcapotzalco');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('C11I', 'C11', 'I', 'Gustavo A. Madero');

-- ---- Sonora (S11) ----
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11A', 'S11', 'A', 'Hermosillo');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11B', 'S11', 'B', 'Ciudad Obregon');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11C', 'S11', 'C', 'Nogales');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11D', 'S11', 'D', 'Guaymas');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11E', 'S11', 'E', 'Navojoa');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11F', 'S11', 'F', 'San Luis Rio Colorado');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11G', 'S11', 'G', 'Caborca');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11H', 'S11', 'H', 'Puerto Penasco');
INSERT INTO municipio (id_municipio, id_estado, letra_municipio, nombre_municipio) VALUES ('S11I', 'S11', 'I', 'Empalme');

-- ══════════════════════════════════════════════════════════════
--  3. GENEROS (10 generos cinematograficos)
-- ══════════════════════════════════════════════════════════════
INSERT INTO genero (id_genero, nombre_genero) VALUES (1, 'Accion');
INSERT INTO genero (id_genero, nombre_genero) VALUES (2, 'Comedia');
INSERT INTO genero (id_genero, nombre_genero) VALUES (3, 'Drama');
INSERT INTO genero (id_genero, nombre_genero) VALUES (4, 'Terror');
INSERT INTO genero (id_genero, nombre_genero) VALUES (5, 'Ciencia Ficcion');
INSERT INTO genero (id_genero, nombre_genero) VALUES (6, 'Animacion');
INSERT INTO genero (id_genero, nombre_genero) VALUES (7, 'Romance');
INSERT INTO genero (id_genero, nombre_genero) VALUES (8, 'Suspenso');
INSERT INTO genero (id_genero, nombre_genero) VALUES (9, 'Documental');
INSERT INTO genero (id_genero, nombre_genero) VALUES (10, 'Aventura');

-- ══════════════════════════════════════════════════════════════
--  4. SALAS
-- ══════════════════════════════════════════════════════════════

-- ---- Jalisco: 3 salas por municipio ----
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (1,  1, 'J11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (2,  2, 'J11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (3,  3, 'J11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (4,  1, 'J11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (5,  2, 'J11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (6,  3, 'J11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (7,  1, 'J11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (8,  2, 'J11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (9,  3, 'J11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (10, 1, 'J11D');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (11, 2, 'J11D');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (12, 3, 'J11D');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (13, 1, 'J11E');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (14, 2, 'J11E');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (15, 3, 'J11E');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (16, 1, 'J11F');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (17, 2, 'J11F');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (18, 1, 'J11G');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (19, 2, 'J11G');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (20, 1, 'J11H');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (21, 2, 'J11H');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (22, 1, 'J11I');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (23, 2, 'J11I');

-- ---- Nuevo Leon: 3 salas en Monterrey, 2 en los demas ----
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (24, 1, 'N11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (25, 2, 'N11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (26, 3, 'N11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (27, 1, 'N11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (28, 2, 'N11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (29, 1, 'N11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (30, 2, 'N11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (31, 1, 'N11D');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (32, 2, 'N11D');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (33, 1, 'N11E');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (34, 2, 'N11E');

-- ---- Estado de Mexico: 2 salas ----
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (35, 1, 'E11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (36, 2, 'E11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (37, 1, 'E11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (38, 2, 'E11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (39, 1, 'E11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (40, 2, 'E11C');

-- ---- CDMX: 2 salas ----
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (41, 1, 'C11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (42, 2, 'C11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (43, 1, 'C11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (44, 2, 'C11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (45, 1, 'C11C');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (46, 2, 'C11C');

-- ---- Sonora: 2 salas ----
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (47, 1, 'S11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (48, 2, 'S11A');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (49, 1, 'S11B');
INSERT INTO sala (id_sala, numero_sala, id_municipio) VALUES (50, 2, 'S11B');

-- ══════════════════════════════════════════════════════════════
--  5. PELICULAS (10 peliculas inventadas bien locas)
--     Clasificaciones: AA, A, B, B15, C, D
-- ══════════════════════════════════════════════════════════════
INSERT INTO pelicula (id_pelicula, nombre, director, productor, clasificacion, duracion_min, id_genero) VALUES
    (1,  'El Mapache que Sabia Kung Fu',          'Carlos Pistolas',     'Don Ramiro Films',       'B',   140, 1),
    (2,  'Burros en el Espacio 2: La Venganza',   'Memo Cacahuate',      'Rancho Studios',         'AA',   95, 2),
    (3,  'Mi Abuela es un Agente Secreto',        'Pepe Dinamita',       'Chancleta Productions',  'A',   175, 3),
    (4,  'La Chancla Maldita',                    'El Cucuy Films',      'Dona Pelos Inc',         'C',   110, 4),
    (5,  'Robots con Bigote',                     'Fritz Tornillo',      'Tuerca Dorada Studios',  'B15', 155, 5),
    (6,  'El Pollo que Queria Volar',             'Kiko Plumas',         'Granja Feliz Animation', 'AA',  100, 6),
    (7,  'Me Enamore de un Cactus',               'Rosa Espinas',        'Desierto Love Films',    'A',   120, 7),
    (8,  'Quien Se Robo Mi Taco',                 'Inspector Salsa',     'Antojitos Noir Studio',  'B15', 130, 8),
    (9,  'La Vida Secreta de los Tamales',        'Doc Maiz',            'Olla Express Docs',      'AA',   90, 9),
    (10, 'Piratas del Lago de Chapala',           'Capitan Mojarra',     'Lanchas Doradas Films',  'A',   135, 10);

-- ══════════════════════════════════════════════════════════════
--  6. USUARIOS
-- ══════════════════════════════════════════════════════════════
-- NOTA: Se tiene que crear un usuario nuevo en la interfaz
--       Ya sea Admin o Cliente

-- ══════════════════════════════════════════════════════════════
--  7. HORARIOS DE CARTELERA
-- ══════════════════════════════════════════════════════════════

-- ---- Guadalajara (J11A) - Sala 1, 2 y 3 ----
-- Sala 1, Fecha: 2026-04-10 (3 peliculas sin empalme)
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (1,  1, 1, '2026-04-10', '10:00', '12:50'),   -- Mapache Kung Fu (140+30)
    (2,  2, 1, '2026-04-10', '13:00', '14:55'),   -- Burros Espacio (95+30)
    (3,  7, 1, '2026-04-10', '15:00', '17:30');    -- Enamore Cactus (120+30)

-- Sala 2, Fecha: 2026-04-10
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (4,  5, 2, '2026-04-10', '11:00', '13:55'),   -- Robots Bigote (155+30)
    (5,  8, 2, '2026-04-10', '14:30', '17:10');    -- Robo Mi Taco (130+30)

-- Sala 3, Fecha: 2026-04-10
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (6,  6, 3, '2026-04-10', '10:00', '12:10'),   -- Pollo Volar (100+30)
    (7,  4, 3, '2026-04-10', '14:00', '15:50');    -- Chancla Maldita (110+30)

-- Sala 1, Fecha: 2026-04-11 (dia siguiente)
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (8,  3, 1, '2026-04-11', '16:00', '19:25'),   -- Abuela Agente (175+30)
    (9,  10,1, '2026-04-11', '10:00', '12:45');    -- Piratas Chapala (135+30)

-- Sala 1, Fecha: 2026-04-12
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (10, 9, 1, '2026-04-12', '11:00', '13:00'),   -- Vida Tamales (90+30)
    (11, 1, 1, '2026-04-12', '15:00', '17:50');    -- Mapache Kung Fu otra vez

-- ---- Zapopan (J11B) ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (12, 1, 4, '2026-04-10', '12:00', '14:50'),
    (13, 6, 4, '2026-04-10', '15:30', '17:40'),
    (14, 2, 5, '2026-04-10', '10:00', '11:55');

-- ---- Tlaquepaque (J11C) ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (15, 5, 7, '2026-04-10', '18:00', '20:55'),
    (16, 7, 8, '2026-04-11', '16:00', '18:30');

-- ---- Monterrey (N11A) ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (17, 1, 24, '2026-04-10', '10:00', '12:50'),
    (18, 3, 24, '2026-04-10', '14:00', '16:55'),
    (19, 5, 25, '2026-04-10', '11:00', '13:55'),
    (20, 8, 25, '2026-04-10', '15:00', '17:40'),
    (21, 6, 26, '2026-04-10', '10:00', '12:10');

-- ---- Toluca (E11A) - Solo una funcion ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (22, 10, 35, '2026-04-10', '17:00', '19:45');

-- ---- CDMX Cuauhtemoc (C11A) ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (23, 4, 41, '2026-04-10', '20:00', '21:50'),
    (24, 3, 42, '2026-04-10', '19:00', '22:25');

-- ---- Hermosillo (S11A) ----
INSERT INTO horario_cartelera (id_horario, id_pelicula, id_sala, fecha_proyeccion, hora_inicio, hora_fin_estimada) VALUES
    (25, 2, 47, '2026-04-10', '10:00', '11:55'),
    (26, 9, 48, '2026-04-10', '12:00', '14:00');

-- ══════════════════════════════════════════════════════════════
-- MUNICIPIOS SIN HORARIOS (para probar casos vacios):
--   - J11D (Tonala) tiene salas pero NO tiene horarios
--   - J11E (Tlajomulco) tiene salas pero NO tiene horarios
--   - N11B (San Pedro) tiene salas pero NO tiene horarios
-- ══════════════════════════════════════════════════════════════

-- ══════════════════════════════════════════════════════════════
-- SECUENCIAS: Ajustar autoincrementales para evitar conflictos
--    - Ejecutar de uno en uno apartir de aquí
-- ══════════════════════════════════════════════════════════════
ALTER SEQUENCE genero_id_genero_seq RESTART WITH 11;
ALTER SEQUENCE pelicula_id_pelicula_seq RESTART WITH 11;
ALTER SEQUENCE sala_id_sala_seq RESTART WITH 51;
ALTER SEQUENCE horario_cartelera_id_horario_seq RESTART WITH 27;
ALTER SEQUENCE usuario_id_usuario_seq RESTART WITH 7;
