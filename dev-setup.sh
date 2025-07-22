#!/bin/bash

# Script de configuración para desarrollo
echo "🚀 Configurando entorno de desarrollo para microservicios Linktic..."

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar que Docker Compose esté instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Instalando con snap..."
    sudo snap install maven --classic
fi

# Compilar ambos servicios
echo "📦 Compilando microservicios..."

echo "Compilando products-service..."
cd products-service
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Error compilando products-service"
    exit 1
fi
cd ..

echo "Compilando inventory-service..."
cd inventory-service
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Error compilando inventory-service"
    exit 1
fi
cd ..

# Detener contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir y levantar servicios con Docker Compose
echo "🐳 Levantando servicios con Docker Compose..."
docker-compose up --build -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios estén listos..."
sleep 45

# Verificar estado de los servicios
echo "🔍 Verificando estado de los servicios..."

# Verificar products-service
if wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health 2>/dev/null; then
    echo "✅ Products Service está funcionando en http://localhost:8081"
else
    echo "❌ Products Service no está respondiendo"
    echo "📋 Logs de products-service:"
    docker-compose logs products-service | tail -10
fi

# Verificar inventory-service
if wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health 2>/dev/null; then
    echo "✅ Inventory Service está funcionando en http://localhost:8080"
else
    echo "❌ Inventory Service no está respondiendo"
    echo "📋 Logs de inventory-service:"
    docker-compose logs inventory-service | tail -10
fi

# Verificar base de datos
if docker exec linktic-postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "✅ PostgreSQL está funcionando"
else
    echo "❌ PostgreSQL no está respondiendo"
    echo "📋 Logs de postgres:"
    docker-compose logs postgres | tail -10
fi

echo ""
echo "🎉 Configuración completada!"
echo ""
echo "📋 URLs de los servicios:"
echo "   Products Service: http://localhost:8081"
echo "   Inventory Service: http://localhost:8080"
echo "   Swagger Products: http://localhost:8081/swagger-ui.html"
echo "   Swagger Inventory: http://localhost:8080/swagger-ui.html"
echo ""
echo "🗄️ Base de datos:"
echo "   Host: localhost"
echo "   Puerto: 5432"
echo "   Base de datos: linktic_db"
echo "   Usuario: postgres"
echo "   Contraseña: password"
echo ""
echo "🔧 Comandos útiles:"
echo "   Ver logs: docker-compose logs -f"
echo "   Ver logs específicos: docker-compose logs -f [service-name]"
echo "   Detener servicios: docker-compose down"
echo "   Reiniciar servicios: docker-compose restart"
echo "   Ver estado: docker-compose ps"
echo ""
echo "🐛 Si hay problemas:"
echo "   1. Verificar logs: docker-compose logs"
echo "   2. Reiniciar: docker-compose restart"
echo "   3. Reconstruir: docker-compose up --build"
echo "" 