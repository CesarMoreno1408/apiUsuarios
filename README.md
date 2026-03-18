# 🔐 API de Usuarios

## Resumen General

**API de Gestión de Usuarios ** es una aplicación REST desarrollada en Spring Boot que proporciona:

✅ **Registro de usuarios** con email y contraseña
✅ **Autenticación JWT** con generación de tokens
✅ **Validación de credenciales** con BCrypt
✅ **Documentación interactiva** con Swagger/OpenAPI
✅ **Mapeo automático de base de datos** con Hibernate
✅ **Seguridad de endpoints** con Spring Security

---

## Tecnologías Utilizadas

### Backend
| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| **Spring Boot** | 3.2.0 | Framework principal |
| **Spring Security** | 6.1 | Autenticación y autorización |
| **Spring Data JPA** | - | Acceso a datos |
| **Hibernate** | 6.3.1 | ORM (Object-Relational Mapping) |
| **JWT (JJWT)** | 0.11.5 | Tokens de autenticación |
| **SQL Server JDBC** | - | Driver para base de datos |
| **Swagger/OpenAPI** | 2.1.0 | Documentación automática |
| **BCrypt** | - | Encriptación de contraseñas |
| **Lombok** | - | Generación de código |



## Funcionalidades Implementadas

### 1. Registro de Usuario ✅
```
POST /api/auth/register
Parámetros: email, password
Respuesta: {"message": "Usuario registrado exitosamente"}
```
- Validación de email único
- Contraseña encriptada con BCrypt
- Almacenamiento en base de datos

### 2. Autenticación (Login) ✅
```
POST /api/auth/login
Parámetros: email, password
Respuesta: {"token": "eyJhbGc..."}
```
- Autenticación con credenciales
- Generación de token JWT
- Token válido por 10 horas
- Incluye email en el token

### 3. Seguridad de Endpoints ✅
```
Header: Authorization: Bearer <token>
```
- Validación automática de tokens
- Endpoints protegidos requieren autenticación
- Filtro JWT en cada request

### 4. Documentación Swagger ✅
```
http://localhost:8080/swagger-ui/index.html
```
- Documentación interactiva
- Descripción de endpoints
- Pruebas directas desde Swagger

### 5. Mapeo de Base de Datos ✅
- Creación automática de tabla `usuarios`
- Hibernate genera DDL automáticamente
- Sincronización con modelos Java

---

## Estructura del Proyecto

```
apiUsuarios/
├── src/
│   ├── main/
│   │   ├── java/com/usta/apiusuarios/
│   │   │   ├── ApiUsuariosApplication.java          # Clase principal
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java              # Spring Security config
│   │   │   │   ├── JwtAuthenticationFilter.java     # Filtro JWT
│   │   │   │   └── SwaggerConfig.java               # Swagger config
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java              # Endpoints REST
│   │   │   │
│   │   │   ├── model/
│   │   │   │   └── Usuario.java                     # Entidad JPA
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── UsuarioRepository.java           # DAO/Repository
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── UsuarioService.java              # Lógica de usuarios
│   │   │   │   └── CustomUserDetailsService.java    # UserDetails para Spring Security
│   │   │   │
│   │   │   └── util/
│   │   │       └── JwtUtil.java                     # Utilidades JWT
│   │   │
│   │   └── resources/
│   │       └── application.properties                # Configuración
│   │
│   └── test/
│       └── java/com/usta/apiusuarios/
│           └── ApiUsuariosApplicationTests.java
│
├── target/                                           # Archivos compilados
├── pom.xml                                           # Dependencias Maven
└── README.md                                         # Esta documentación
```



---
## Link de Acceso a la api en Azure
https://apiusers-d8drfrcbawbmekgm.brazilsouth-01.azurewebsites.net/swagger-ui/index.html#/Autenticacion/login
