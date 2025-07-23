-- Script de inicialización de la base de datos
-- Crea las tablas necesarias para ambos microservicios

-- Crear esquema para productos
CREATE SCHEMA IF NOT EXISTS products;

-- Crear esquema para inventario
CREATE SCHEMA IF NOT EXISTS inventory;

-- Tabla de productos
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de inventario
CREATE TABLE IF NOT EXISTS inventory (
    product_id UUID PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Tabla de compras
CREATE TABLE IF NOT EXISTS purchases (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Crear índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_purchases_product_id ON purchases(product_id);
CREATE INDEX IF NOT EXISTS idx_purchases_created_at ON purchases(created_at);

-- Insertar datos de ejemplo (opcional)
INSERT INTO products (id, name, price, description) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'Laptop Gaming', 1299.99, 'Laptop para gaming de alto rendimiento'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Mouse Inalámbrico', 29.99, 'Mouse inalámbrico ergonómico'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Teclado Mecánico', 89.99, 'Teclado mecánico RGB')
ON CONFLICT (id) DO NOTHING;

-- Insertar inventario inicial
INSERT INTO inventory (product_id, quantity) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 10),
    ('550e8400-e29b-41d4-a716-446655440002', 50),
    ('550e8400-e29b-41d4-a716-446655440003', 25)
ON CONFLICT (product_id) DO NOTHING; 