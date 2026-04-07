# 🍿 CineWax - Sistema de Cartelera de Cines

CineWax es un sistema backend desarrollado en ```Java (Spring Boot)``` diseñado para gestionar la cartelera de una cadena de cines a nivel estatal. El sistema permite administrar películas, horarios, salas y sucursales (municipios), ofreciendo una interfaz interactiva por consola para Administradores y Clientes.

---

## 🛠️ 1. Tecnologías y Versiones Requeridas

Para poder levantar este proyecto en un entorno local, necesitas tener instaladas las siguientes herramientas:

* **Java Development Kit (JDK):** Versión 17 (Estrictamente requerido).
* **Docker Desktop:** Para levantar los contenedores de la base de datos.
* **Git:** Para clonar el repositorio.
* **IDE Recomendado:** VS Code (con ```Extension Pack for Java``` y ```Spring Boot Extension Pack```), IntelliJ IDEA o Eclipse.

---

## 📦 2. Descarga e Instalación

### Paso 1: Obtener el código
Clona el repositorio en tu máquina local o descarga el código fuente y descomprímelo.
```bash
git clone <url-de-tu-repositorio>
cd CineWax
```

### Paso 2: Preparar la Base de Datos (Docker)
El proyecto utiliza PostgreSQL. No necesitas instalarlo en tu computadora, la configuración ya está lista mediante Docker Compose.

Abre tu terminal en la raíz del proyecto y ejecuta:
``````bash
docker compose up -d postgres pgadmin
``````
* **```postgres```**: Levanta el motor de base de datos en el puerto ```5432```.
* **```pgadmin```**: Levanta un cliente visual web en ```http://localhost:5050``` (Credenciales por defecto en el archivo ```Compose.yml```).

*Nota: La aplicación cuenta con un archivo ```data.sql``` que poblará automáticamente los catálogos base (Estados, Municipios, Géneros y Salas) la primera vez que se levante la aplicación.*

### Paso 3: Ejecutar la Aplicación
Puedes arrancar la aplicación de dos maneras:

**Opción A (Desde Terminal con Gradle):**
Asegúrate de permitir la entrada del teclado en la configuración de gradle y ejecuta:
```bash
./gradlew bootRun
```

**Opción B (Desde el IDE):**
Abre la clase principal ```src/main/java/com/waxeados/CineWax/CineWaxApplication.java``` y presiona el botón de **Play/Run**.

---

## 🏛️ 3. Arquitectura del Proyecto

El proyecto sigue una **Arquitectura en Capas (Layered Architecture)** estándar de Spring Boot, separando claramente las responsabilidades del sistema.

### Estructura de Directorios
* **```console/``` (Presentación):** Contiene ```ConsoleRunner.java```. Es la interfaz interactiva de línea de comandos que intercepta los inputs del usuario y llama a los servicios.
* **```controllers/``` (API REST):** Puntos de entrada HTTP (Endpoints) diseñados para una futura integración con un Frontend web/móvil.
* **```services/``` (Lógica de Negocio):** Contiene todas las reglas de negocio, validaciones y la orquestación del uso de Estructuras de Datos Personalizadas.
* **```repositories/``` (Acceso a Datos):** Interfaces de Spring Data JPA que abstraen las consultas SQL a la base de datos PostgreSQL.
* **```entity/``` (Modelo de Datos):** Clases mapeadas directamente a las tablas de la base de datos utilizando anotaciones de Hibernate/JPA.
* **```dto/``` (Objetos de Transferencia):** Objetos planos utilizados para mover información entre las capas sin exponer directamente las entidades de la base de datos.
* **```mappers/```**: Componentes encargados de transformar objetos ```Entity``` a ```DTO``` y viceversa.
* **```exceptions/```**: Manejo globalizado de errores (```GlobalExceptionHandler```) y excepciones personalizadas (```HorarioEmpalmadoException```).
* **```structures/```**: Implementación "Desde Cero" de estructuras de datos clásicas aplicadas a la lógica de negocio.

### Flujo de Comunicación (Ejemplo de Consulta de Cartelera)
1.  **Input:** El usuario interactúa con ```ConsoleRunner``` y solicita ver la cartelera de un municipio.
2.  **Servicio:** ```ConsoleRunner``` llama a ```HorarioService.consultarCartelera(idMunicipio)```.
3.  **Persistencia:** El servicio pide los datos crudos al ```HorarioCarteleraRepository```.
4.  **Procesamiento:** El servicio ordena los datos en memoria utilizando la estructura ```QuickSort```.
5.  **Mapeo:** Se utiliza ```HorarioMapper``` para convertir las entidades resultantes en ```CarteleraDTO```.
6.  **Output:** El servicio devuelve la lista de DTOs al ```ConsoleRunner```, el cual los imprime formateados en pantalla.

---

## 🧮 4. Estructuras de Datos y Algoritmos Utilizados

El sistema integra algoritmos y estructuras de datos clásicas para resolver problemas específicos del negocio en memoria:

* **Pila (Stack):** Utilizada en ```HistorialService``` para almacenar las acciones de navegación del cliente bajo el principio LIFO (Last-In, First-Out), permitiendo la función de "Regresar a la acción anterior".
* **Cola (Queue):** Utilizada en ```HorarioService``` para encolar solicitudes de alta de horarios por parte de los administradores. Garantiza el principio FIFO (First-In, First-Out) para procesar las altas secuencialmente y evitar empalmes de concurrencia.
* **Lista Enlazada Simple:** Utilizada a lo largo del sistema para búsquedas en memoria (iterativas y recursivas) sobre catálogos de películas.
* **QuickSort:** Algoritmo de ordenamiento O(n log n) implementado para organizar las carteleras de horarios cronológicamente (por fecha y hora) de manera ascendente o descendente.
* **Floyd-Warshall (Grafos):** Implementado en ```FloydService``` para modelar los municipios de un estado como un grafo. Calcula la matriz de distancias mínimas para sugerir cines cercanos al usuario y trazar la ruta más corta entre sucursales.

---

## 🔒 5. Notas de Seguridad y Credenciales

* **Contraseñas:** Las contraseñas de los usuarios nunca se guardan en texto plano; se utiliza ```BCryptPasswordEncoder``` para aplicar un hash criptográfico en la base de datos.
* **Tipos de Usuario:** El sistema diferencia entre roles (```ADMINISTRADOR``` y ```CLIENTE```). Los administradores tienen restricciones de alcance ligadas a su ```idMunicipio```.
