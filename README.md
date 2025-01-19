# Reto Alura Latam | Oracle Next Education

## Descripcion

**Aplicación en Java que permite la gestión de libros ya autores usando la API pública de [Gutendex](https://gutendex.com/)
donde la interacción textual (vía consola), puede realizar búsquedas por nombre libro, autores, fecha de nacimiento
entre otras funciones.**

## OBJETIVO:
**Desarrollar un Catálogo de Libros que ofrezca interacción textual (vía consola) con los usuarios, 
proporcionando al menos 5 opciones de interacción. Los libros se buscarán a través de una API específica. 
La información sobre la API y las opciones de interacción con el usuario se detallará en la columna "Backlog"/"Listo para iniciar".**

### Los pasos para completar este desafío se detallarán a continuación y estarán disponibles en la sección adyacente:
1. **Configuración del Ambiente Java;**
2. **Creación del Proyecto;**
3. **Consumo de la API;**
4. **Análisis de la Respuesta JSON;**
5. **Inserción y consulta en la base de datos;**
6. **Exibición de resultados a los usuarios;**

## Requisitos:
    -Java 17 o superior
    -Spirnt Boot 3.0 o superior
    -PostgreSQL 13 o superior
    
## Dependencias y Características Técnicas

    -Spring Data JPA
    -PostgreSQL Driver
    -Java 17
    -Spring Boot DevTools
    -Jackson
    -spring-boot-maven-plugin

## Configuración

### **Configura PostgreSQL:**
1. Cree base de datos "LiteraAlura"
2. Cree su variables de entorno de username y password. (Opcional)
3. En **src\main\resources\application.properties** reemplazar por su información:
```
spring.datasource.url=jdbc:postgresql://${DB_HOST}/LiteraAlura 
spring.datasource.username=${DB_USER_POSTGRE}
spring.datasource.password=${DB_PASSWORD_POSTGRE}
```

### Funcionalidades:
1. **Buscar Libro por nombre**
2. **Mostrar Libros registrados**
3. **Mostrar autores registrados**
4. **Mostrar autores vivos por años**
5. **Mostrar por idiomas**
6. **Generar estadísticas de libros**
7. **Mostrar Top 10 libros más descargados**
8. **Buscar autor por nombre**
9. **Listar autores por año de nacimiento o fallecimiento**



