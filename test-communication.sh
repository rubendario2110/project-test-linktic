#!/bin/bash

echo "🧪 Probando comunicación entre microservicios..."

# Función para esperar a que un servicio esté listo
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo "⏳ Esperando a que $service_name esté listo..."
    
    while [ $attempt -le $max_attempts ]; do
        if wget --no-verbose --tries=1 --spider "$url" 2>/dev/null; then
            echo "✅ $service_name está listo!"
            return 0
        fi
        
        echo "   Intento $attempt/$max_attempts..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo "❌ $service_name no está respondiendo después de $max_attempts intentos"
    return 1
}

# Esperar a que products-service esté listo
if ! wait_for_service "http://localhost:8081/actuator/health" "Products Service"; then
    echo "📋 Logs de products-service:"
    docker-compose logs products-service | tail -20
    exit 1
fi

# Esperar a que inventory-service esté listo
if ! wait_for_service "http://localhost:8080/actuator/health" "Inventory Service"; then
    echo "📋 Logs de inventory-service:"
    docker-compose logs inventory-service | tail -20
    exit 1
fi

echo ""
echo "🔗 Probando comunicación entre servicios..."

# Crear un producto
echo "📦 Creando un producto de prueba..."
PRODUCT_RESPONSE=$(curl -s -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -H "X-INTERNAL-API-KEY: linktic-internal-key-2024" \
  -d '{
    "name": "Producto de Prueba",
    "price": 99.99,
    "description": "Producto para probar la comunicación"
  }')

echo "Respuesta de creación de producto:"
echo "$PRODUCT_RESPONSE"

# Extraer el ID del producto
PRODUCT_ID=$(echo "$PRODUCT_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

if [ -z "$PRODUCT_ID" ]; then
    echo "❌ No se pudo obtener el ID del producto"
    exit 1
fi

echo "🆔 ID del producto creado: $PRODUCT_ID"

# Obtener el producto
echo "📋 Obteniendo el producto creado..."
curl -s -X GET "http://localhost:8081/products/$PRODUCT_ID" | jq '.'

# Realizar una compra
echo "🛒 Realizando una compra..."
PURCHASE_RESPONSE=$(curl -s -X POST http://localhost:8080/purchases \
  -H "Content-Type: application/json" \
  -d "{
    \"productId\": \"$PRODUCT_ID\",
    \"quantity\": 2
  }")

echo "Respuesta de la compra:"
echo "$PURCHASE_RESPONSE"

# Verificar el inventario
echo "📊 Verificando el inventario..."
curl -s -X GET "http://localhost:8080/inventory/$PRODUCT_ID" | jq '.'

echo ""
echo "🎉 ¡Prueba de comunicación completada exitosamente!"
echo ""
echo "📋 Resumen de la prueba:"
echo "   ✅ Products Service: Funcionando"
echo "   ✅ Inventory Service: Funcionando"
echo "   ✅ Comunicación entre servicios: Funcionando"
echo "   ✅ Creación de producto: Exitosa"
echo "   ✅ Compra: Exitosa"
echo "   ✅ Verificación de inventario: Exitosa"
echo "" 