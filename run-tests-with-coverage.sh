#!/bin/bash

echo "🚀 Ejecutando pruebas y generando reportes de cobertura..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar el progreso
show_progress() {
    echo -e "${BLUE}📊 $1${NC}"
}

# Función para mostrar éxito
show_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Función para mostrar error
show_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Función para mostrar advertencia
show_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Limpiar y compilar
show_progress "Limpiando y compilando proyectos..."

cd products-service
mvn clean compile -q
if [ $? -ne 0 ]; then
    show_error "Error compilando products-service"
    exit 1
fi

cd ../inventory-service
mvn clean compile -q
if [ $? -ne 0 ]; then
    show_error "Error compilando inventory-service"
    exit 1
fi

cd ..

# Ejecutar pruebas del servicio de productos
show_progress "Ejecutando pruebas del servicio de productos..."
cd products-service
mvn test -q
if [ $? -ne 0 ]; then
    show_error "Error en las pruebas del servicio de productos"
    exit 1
fi

# Verificar cobertura del servicio de productos
if [ -f "target/site/jacoco/index.html" ]; then
    show_success "Reporte de cobertura generado para products-service"
    echo "📄 Reporte disponible en: products-service/target/site/jacoco/index.html"
else
    show_warning "No se pudo generar el reporte de cobertura para products-service"
fi

cd ..

# Ejecutar pruebas del servicio de inventario
show_progress "Ejecutando pruebas del servicio de inventario..."
cd inventory-service
mvn test -q
if [ $? -ne 0 ]; then
    show_error "Error en las pruebas del servicio de inventario"
    exit 1
fi

# Verificar cobertura del servicio de inventario
if [ -f "target/site/jacoco/index.html" ]; then
    show_success "Reporte de cobertura generado para inventory-service"
    echo "📄 Reporte disponible en: inventory-service/target/site/jacoco/index.html"
else
    show_warning "No se pudo generar el reporte de cobertura para inventory-service"
fi

cd ..

# Mostrar resumen
echo ""
echo "🎉 Resumen de ejecución:"
echo "=========================="
echo "✅ Products Service: Pruebas ejecutadas correctamente"
echo "✅ Inventory Service: Pruebas ejecutadas correctamente"
echo ""
echo "📊 Reportes de cobertura generados:"
echo "   - Products Service: products-service/target/site/jacoco/index.html"
echo "   - Inventory Service: inventory-service/target/site/jacoco/index.html"
echo ""
echo "🔍 Para ver los reportes de cobertura, abre los archivos HTML en tu navegador"
echo "📈 Objetivo de cobertura: 80%"
echo ""
show_success "¡Todas las pruebas ejecutadas exitosamente!" 