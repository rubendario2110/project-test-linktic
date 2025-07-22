# Microservicios Linktic - Test Backend

## 📋 Descripción

Sistema de microservicios que implementa la gestión de productos e inventario con comunicación entre servicios. El proyecto incluye:

- **Products Service**: Gestión de catálogo de productos
- **Inventory Service**: Gestión de inventario y compras
- **Comunicación HTTP**: Entre servicios usando OpenFeign
- **Base de datos**: PostgreSQL compartida
- **Containerización**: Docker y Docker Compose

## 🏗️ Arquitectura

```
┌─────────────────┐    HTTP/JSON    ┌─────────────────┐
│   Inventory     │ ◄──────────────► │   Products      │
│   Service       │   API Key Auth  │   Service       │
│   (Port 8080)   │                 │   (Port 8081)   │
└─────────────────┘                 └─────────────────┘
         │                                    │
         ▼                                    ▼
┌─────────────────┐                 ┌─────────────────┐
│   PostgreSQL    │                 │   PostgreSQL    │
│   Database      │                 │   Database      │
│   (Port 5432)   │                 │   (Port 5432)   │
└─────────────────┘                 └─────────────────┘
```

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenFeign** (Comunicación entre servicios)
- **Docker & Docker Compose**
- **Maven**

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

### 2. Ejecutar script de configuración
```bash
# Dar permisos de ejecución
chmod +x dev-setup.sh

# Ejecutar configuración
./dev-setup.sh
```

### 3. Verificar servicios
```bash
# Ver estado de contenedores
docker-compose ps

# Ver logs
docker-compose logs -f

# Probar comunicación entre servicios
chmod +x test-communication.sh
./test-communication.sh
```

## 🔧 Configuración Manual

### 1. Compilar servicios
```bash
# Products Service
cd products-service
mvn clean package -DskipTests
cd ..

# Inventory Service
cd inventory-service
mvn clean package -DskipTests
cd ..
```

### 2. Levantar con Docker Compose
```bash
# Construir y levantar todos los servicios
docker-compose up --build -d

# Ver logs
docker-compose logs -f
```

### 3. Variables de entorno
```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/linktic_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# API Key para comunicación entre servicios
INTERNAL_API_KEY=linktic-internal-key-2024

# URLs de servicios
PRODUCTS_SERVICE_BASE_URL=http://products-service:8081
```

## 📚 API Documentation

### URLs de acceso
- **Products Service**: http://localhost:8081
- **Inventory Service**: http://localhost:8080
- **Swagger Products**: http://localhost:8081/swagger-ui.html
- **Swagger Inventory**: http://localhost:8080/swagger-ui.html

### Endpoints principales

#### Products Service
```http
# Crear producto
POST /products
Content-Type: application/json
X-INTERNAL-API-KEY: linktic-internal-key-2024

{
  "name": "Laptop Gaming",
  "price": 1299.99,
  "description": "Laptop para gaming"
}

# Obtener producto
GET /products/{id}

# Listar productos
GET /products?page=0&size=20
```

#### Inventory Service
```http
# Consultar inventario
GET /inventory/{productId}

# Actualizar inventario
PATCH /inventory/{productId}
Content-Type: application/json

{
  "data": {
    "attributes": {
      "quantity": 10
    }
  }
}

# Realizar compra
POST /purchases
Content-Type: application/json

{
  "productId": "550e8400-e29b-41d4-a716-446655440001",
  "quantity": 2
}
```

## 🧪 Testing

### Tests unitarios
```bash
# Products Service
cd products-service
mvn test

# Inventory Service
cd inventory-service
mvn test
```

### Tests de integración
```bash
# Probar comunicación entre servicios
./test-communication.sh
```

### Tests manuales
```bash
# Crear producto
curl -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -H "X-INTERNAL-API-KEY: linktic-internal-key-2024" \
  -d '{"name": "Test Product", "price": 99.99}'

# Realizar compra
curl -X POST http://localhost:8080/purchases \
  -H "Content-Type: application/json" \
  -d '{"productId": "PRODUCT_ID", "quantity": 1}'
```

## 🔍 Monitoreo

### Health Checks
```bash
# Products Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8080/actuator/health
```

### Logs
```bash
# Ver todos los logs
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f products-service
docker-compose logs -f inventory-service
docker-compose logs -f postgres
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

# Construir JAR
mvn clean package

# Ejecutar localmente
mvn spring-boot:run
```

## 🐛 Troubleshooting

### Problemas comunes

1. **Error de puerto ocupado**
   ```bash
   # Verificar puertos en uso
   sudo netstat -tulpn | grep :8080
   sudo netstat -tulpn | grep :8081
   
   # Detener servicios
   docker-compose down
   ```

2. **Error de base de datos**
   ```bash
   # Verificar PostgreSQL
   docker-compose logs postgres
   
   # Reiniciar base de datos
   docker-compose restart postgres
   ```

3. **Error de comunicación entre servicios**
   ```bash
   # Verificar red
   docker network ls
   docker network inspect project-test-linktic_linktic-network
   
   # Verificar logs
   docker-compose logs products-service
   docker-compose logs inventory-service
   ```

4. **Error de Maven**
   ```bash
   # Limpiar cache
   mvn clean
   
   # Actualizar dependencias
   mvn dependency:resolve
   ```

## 📊 Estructura del Proyecto

```
project-test-linktic/
├── products-service/          # Microservicio de productos
│   ├── src/main/java/
│   ├── src/test/java/
│   ├── Dockerfile
│   └── pom.xml
├── inventory-service/         # Microservicio de inventario
│   ├── src/main/java/
│   ├── src/test/java/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml         # Configuración de contenedores
├── init-db.sql               # Script de inicialización de BD
├── dev-setup.sh              # Script de configuración
├── test-communication.sh     # Script de pruebas
└── README.md
```

## 🔮 Mejoras Futuras

- [ ] Implementar Circuit Breaker con Resilience4j
- [ ] Agregar métricas con Micrometer
- [ ] Implementar logging estructurado
- [ ] Agregar tests de integración completos
- [ ] Implementar CI/CD pipeline
- [ ] Agregar documentación con Postman
- [ ] Implementar autenticación JWT
- [ ] Agregar caché con Redis

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.