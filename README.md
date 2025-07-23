# Microservicios Linktic - Test Backend

## 📋 Descripción

Sistema de microservicios que implementa la gestión de productos e inventario con comunicación entre servicios, siguiendo el estándar JSON API. El proyecto incluye:

- **Products Service**: Gestión de catálogo de productos
- **Inventory Service**: Gestión de inventario y compras
- **API Gateway**: Punto de entrada único con routing y autenticación
- **Eureka Server**: Service discovery para microservicios
- **Comunicación HTTP**: Entre servicios usando RestTemplate con LoadBalancer
- **Base de datos**: PostgreSQL compartida
- **Containerización**: Docker y Docker Compose
- **Testing**: Pruebas unitarias y de integración con 80% de cobertura
- **Documentación**: Swagger/OpenAPI integrado
- **Cobertura de Código**: JaCoCo con reportes HTML

## 🏗️ Arquitectura

```
┌─────────────────┐    HTTP/JSON API    ┌─────────────────┐
│   API Gateway   │ ◄──────────────────► │   Eureka        │
│   (Port 8080)   │   Service Discovery │   Server        │
│                 │                     │   (Port 8761)   │
│   - Routing     │                     │                 │
│   - Auth Filter │                     │                 │
└─────────────────┘                     └─────────────────┘
         │                                        ▲
         ▼                                        │
┌─────────────────┐    HTTP/JSON API    ┌─────────────────┐
│   Inventory     │ ◄──────────────────► │   Products      │
│   Service       │   RestTemplate +    │   Service       │
│   (Port 8082)   │   LoadBalancer      │   (Port 8081)   │
│                 │                     │                 │
│   - Inventory   │                     │   - Products    │
│   - Purchases   │                     │   - Catalog     │
└─────────────────┘                     └─────────────────┘
         │                                        │
         ▼                                        ▼
┌─────────────────┐                     ┌─────────────────┐
│   PostgreSQL    │                     │   PostgreSQL    │
│   Database      │                     │   Database      │
│   (Port 5432)   │                     │   (Port 5432)   │
└─────────────────┘                     └─────────────────┘
```

## 🎯 Decisiones Técnicas y Justificaciones

### 1. **Lenguaje y Framework**
- **Java 17 + Spring Boot 3.2.5**: Elección basada en la vacante aplicada
- **Spring Data JPA**: Para persistencia de datos con Hibernate
- **Maven**: Gestión de dependencias y build

### 2. **Base de Datos**
- **PostgreSQL**: Elección justificada por:
  - Consistencia ACID para transacciones de inventario
  - Soporte robusto para relaciones entre entidades
  - Escalabilidad y confiabilidad en producción
  - Mejor rendimiento que SQLite para aplicaciones multi-usuario

### 3. **Comunicación entre Microservicios**
- **HTTP REST + JSON API**: Estándar JSON API para todas las respuestas
- **RestTemplate con @LoadBalanced**: Cliente HTTP para comunicación entre servicios
- **Eureka Service Discovery**: Registro y descubrimiento automático de servicios
- **API Gateway**: Punto de entrada único con routing y filtros
- **API Key Authentication**: Autenticación básica entre servicios

### 4. **Endpoint de Compra - Decisión Arquitectónica**

**Decisión**: Implementar el endpoint de compra en el **Inventory Service**

**Justificación**:
- **Responsabilidad única**: El Inventory Service es responsable de la gestión de inventario
- **Consistencia de datos**: Las operaciones de inventario y compra están en el mismo servicio
- **Menor acoplamiento**: El Products Service no necesita conocer la lógica de compras
- **Patrón de diseño**: Sigue el patrón de "Bounded Context" de Domain-Driven Design

**Flujo de compra implementado**:
1. Inventory Service recibe solicitud de compra
2. Consulta información del producto al Products Service via RestTemplate
3. Valida disponibilidad en inventario
4. Actualiza cantidad disponible
5. Registra la compra
6. Retorna información de la compra

### 5. **Testing y Cobertura de Código**
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para pruebas unitarias
- **Spring Boot Test**: Testing de integración
- **JaCoCo**: Análisis de cobertura de código (objetivo: 80%)
- **H2 Database**: Base de datos en memoria para tests

### 6. **Documentación API**
- **Swagger/OpenAPI 3**: Documentación automática de APIs
- **SpringDoc**: Integración con Spring Boot
- **UI Interactiva**: Interfaz web para probar endpoints

### 7. **Containerización**
- **Docker**: Para containerización individual de servicios
- **Docker Compose**: Para orquestación y networking entre servicios

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway** (API Gateway)
- **Spring Data JPA**
- **RestTemplate con LoadBalancer** (Comunicación entre servicios)
- **PostgreSQL 15**
- **H2 Database** (Testing)
- **Docker & Docker Compose**
- **Maven**
- **JUnit 5 + Mockito** (Testing)
- **JaCoCo** (Cobertura de código)
- **Swagger/OpenAPI 3** (Documentación API)

## 📋 Prerrequisitos

### Instalar Maven
```bash
# Opción 1: Con Snap (Recomendado)
sudo snap install maven --classic

# Opción 2: Con apt
sudo apt update
sudo apt install maven

# Verificar instalación
mvn --version
```

### Instalar Docker y Docker Compose
```bash
# Docker
sudo apt update
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# Docker Compose
sudo apt install docker-compose

# Verificar instalación
docker --version
docker-compose --version
```

## 🐳 Configuración Rápida con Docker Compose

### 1. Clonar y configurar
```bash
git clone <repository-url>
cd project-test-linktic
```

### 2. Ejecutar servicios
```bash
# Construir y levantar todos los servicios
docker-compose up --build -d

# Ver logs
docker-compose logs -f
```

### 3. Verificar servicios
```bash
# Ver estado de contenedores
docker-compose ps

# Verificar Eureka
curl http://localhost:8761

# Verificar API Gateway
curl http://localhost:8080

# Probar endpoints directos
curl http://localhost:8081/products
curl http://localhost:8082/inventory/550e8400-e29b-41d4-a716-446655440000

# Probar endpoints a través del Gateway
curl http://localhost:8080/api/products
curl http://localhost:8080/api/inventory/550e8400-e29b-41d4-a716-446655440000
```

## 📚 API Documentation

### URLs de acceso
- **API Gateway**: http://localhost:8080
- **Eureka Server**: http://localhost:8761
- **Products Service**: http://localhost:8081
- **Inventory Service**: http://localhost:8082
- **Swagger Products**: http://localhost:8081/swagger-ui/index.html
- **Swagger Inventory**: http://localhost:8082/swagger-ui/index.html

> 📖 **Guía completa de Swagger**: Consulta [SWAGGER_GUIDE.md](SWAGGER_GUIDE.md) para instrucciones detalladas de uso.

### Endpoints principales

#### Products Service (JSON API)
```http
# Crear producto
POST /api/products
Content-Type: application/json
X-INTERNAL-API-KEY: linktic-internal-key-2024

{
  "name": "Laptop Gaming",
  "price": 1299.99,
  "description": "Laptop para gaming"
}

# Respuesta JSON API
{
  "data": {
    "type": "product",
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "attributes": {
      "name": "Laptop Gaming",
      "price": 1299.99,
      "description": "Laptop para gaming",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  }
}

# Obtener producto
GET /api/products/{id}
X-INTERNAL-API-KEY: linktic-internal-key-2024

# Listar productos
GET /api/products?page=0&size=20
X-INTERNAL-API-KEY: linktic-internal-key-2024
```

#### Inventory Service (JSON API)
```http
# Consultar inventario
GET /api/inventory/{productId}
X-INTERNAL-API-KEY: linktic-internal-key-2024

# Respuesta JSON API
{
  "data": {
    "type": "inventory",
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "attributes": {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 10
    }
  }
}

# Actualizar inventario
PATCH /api/inventory/{productId}
Content-Type: application/json
X-INTERNAL-API-KEY: linktic-internal-key-2024

{
  "data": {
    "attributes": {
      "quantity": 10
    }
  }
}

# Realizar compra
POST /api/purchases
Content-Type: application/json
X-INTERNAL-API-KEY: linktic-internal-key-2024

{
  "productId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 2
}

# Respuesta JSON API
{
  "data": {
    "type": "purchase",
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "attributes": {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "unitPrice": 1299.99,
      "totalPrice": 2599.98,
      "createdAt": "2024-01-15T10:30:00Z"
    }
  }
}
```

## 🧪 Testing y Cobertura de Código

### Ejecutar Tests y Generar Cobertura
```bash
# Ejecutar script completo de testing y cobertura
./run-tests-with-coverage.sh

# O ejecutar individualmente por servicio
cd products-service
mvn test

cd ../inventory-service
mvn test
```

### Ver Reportes de Cobertura
```bash
# Abrir reportes en navegador
# Products Service
open products-service/target/site/jacoco/index.html

# Inventory Service
open inventory-service/target/site/jacoco/index.html
```

### Tests manuales
```bash
# Crear producto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-INTERNAL-API-KEY: linktic-internal-key-2024" \
  -d '{"name": "Test Product", "price": 99.99, "description": "Test Description"}'

# Realizar compra
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -H "X-INTERNAL-API-KEY: linktic-internal-key-2024" \
  -d '{"productId": "550e8400-e29b-41d4-a716-446655440001", "quantity": 2}'
```

### Tipos de Tests Implementados

#### Products Service
- **Unit Tests**: `ProductServiceTest`, `ProductControllerTest`
- **Integration Tests**: `ProductIntegrationTest`
- **Cobertura objetivo**: 80%

#### Inventory Service
- **Unit Tests**: `InventoryServiceTest`, `PurchaseServiceTest`, `InventoryControllerTest`, `PurchaseControllerTest`
- **Integration Tests**: `InventoryIntegrationTest`
- **Cobertura objetivo**: 80%

## 🔍 Monitoreo

### Health Checks
```bash
# Products Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8082/actuator/health
```

### Logs
```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f products-service
docker-compose logs -f inventory-service
docker-compose logs -f postgres_db
```

## 🛠️ Comandos útiles

### Docker Compose
```bash
# Levantar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Reconstruir servicios
docker-compose up --build -d

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f [service-name]
```

### Desarrollo
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn clean test jacoco:report

# Construir JAR
mvn clean package

# Ejecutar localmente
mvn spring-boot:run
```

### Testing y Cobertura
```bash
# Ejecutar script completo
./run-tests-with-coverage.sh

# Verificar cobertura
mvn jacoco:check

# Generar reporte de cobertura
mvn jacoco:report
```

## 🤖 Uso de Herramientas de IA en el Desarrollo

### Herramientas Utilizadas

1. **GitHub Copilot**
   - **Tareas**: Generación de código boilerplate, sugerencias de métodos
   - **Verificación**: Revisión manual de código generado, pruebas unitarias
   - **Beneficios**: Aceleración en desarrollo de DTOs y entidades

2. **Cursor AI**
   - **Tareas**: Refactoring de código, generación de tests
   - **Verificación**: Ejecución de tests, revisión de lógica de negocio
   - **Beneficios**: Mejora en calidad de código y cobertura de tests

3. **ChatGPT**
   - **Tareas**: Diseño de arquitectura, decisiones técnicas
   - **Verificación**: Implementación y testing de decisiones
   - **Beneficios**: Validación de patrones de diseño y mejores prácticas

### Proceso de Verificación de Calidad

1. **Revisión de código**: Análisis manual de todo el código generado
2. **Testing manual**: Verificación de endpoints y flujos completos
3. **Validación de comunicación**: Pruebas de integración entre servicios
4. **Code review**: Revisión de patrones y mejores prácticas

### Mejores Prácticas Aplicadas

- **Validación manual**: Todo el código generado por IA es revisado manualmente
- **Testing exhaustivo**: Implementación de tests unitarios y de integración
- **Documentación**: Generación de documentación técnica detallada
- **Arquitectura**: Validación de decisiones arquitectónicas con patrones establecidos

## 📊 Estructura del Proyecto

```
project-test-linktic/
├── api-gateway/              # API Gateway con routing y autenticación
│   ├── src/main/java/
│   │   └── com/testbackend/gateway/
│   │       ├── config/       # Configuraciones del gateway
│   │       └── ApiGatewayApplication.java
│   ├── src/main/resources/
│   │   └── application.yml   # Configuración de rutas
│   ├── Dockerfile
│   └── pom.xml
├── eureka-server/            # Service Discovery
│   ├── src/main/java/
│   │   └── com/testbackend/eureka/
│   │       └── EurekaServerApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── products-service/          # Microservicio de productos
│   ├── src/main/java/
│   │   ├── controller/        # Controladores REST
│   │   ├── service/          # Lógica de negocio
│   │   ├── repository/       # Acceso a datos
│   │   ├── domain/           # Entidades JPA
│   │   ├── dto/              # Data Transfer Objects
│   │   └── config/           # Configuraciones
│   ├── src/test/java/        # Tests unitarios e integración
│   ├── src/test/resources/   # Configuración de tests
│   ├── Dockerfile
│   └── pom.xml
├── inventory-service/         # Microservicio de inventario
│   ├── src/main/java/
│   │   ├── controller/        # Controladores REST
│   │   ├── service/          # Lógica de negocio
│   │   ├── repository/       # Acceso a datos
│   │   ├── domain/           # Entidades JPA
│   │   ├── dto/              # Data Transfer Objects
│   │   └── config/           # Configuraciones (RestTemplate)
│   ├── src/test/java/        # Tests unitarios e integración
│   ├── src/test/resources/   # Configuración de tests
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml         # Configuración de contenedores
├── init-db.sql               # Script de inicialización de BD
├── run-tests-with-coverage.sh # Script para ejecutar tests y cobertura
├── Linktic-Microservices.postman_collection.json # Colección Postman
├── SWAGGER_GUIDE.md          # Guía completa de uso de Swagger
└── README.md
```

## 🔮 Mejoras Futuras

- [x] ✅ Implementar comunicación entre servicios con RestTemplate
- [x] ✅ Agregar tests unitarios y de integración
- [x] ✅ Implementar cobertura de código con JaCoCo
- [x] ✅ Configurar Swagger/OpenAPI para documentación
- [x] ✅ Implementar API Gateway con routing
- [x] ✅ Configurar Service Discovery con Eureka
- [ ] Implementar Circuit Breaker con Resilience4j
- [ ] Agregar métricas con Micrometer
- [ ] Implementar logging estructurado
- [ ] Implementar CI/CD pipeline
- [ ] Implementar autenticación JWT
- [ ] Agregar caché con Redis
- [ ] Implementar eventos de dominio
- [ ] Agregar monitoreo con Prometheus/Grafana

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.