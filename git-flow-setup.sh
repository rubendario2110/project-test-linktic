#!/bin/bash

echo "=== Configurando Git Flow para el proyecto ==="
echo ""

# Verificar si estamos en un repositorio Git
if [ ! -d ".git" ]; then
    echo "❌ Error: No se encontró un repositorio Git"
    echo "Por favor, inicializa Git primero: git init"
    exit 1
fi

# Configurar Git Flow
echo "1. Configurando Git Flow..."
git flow init -d

echo ""
echo "2. Creando rama de desarrollo..."
git checkout -b develop

echo ""
echo "3. Configurando ramas remotas..."
git push -u origin main
git push -u origin develop

echo ""
echo "4. Creando rama feature para el test técnico..."
git checkout -b feature/test-technical-implementation

echo ""
echo "✅ Git Flow configurado correctamente"
echo ""
echo "📋 Comandos útiles de Git Flow:"
echo "  - Crear feature: git flow feature start nombre-feature"
echo "  - Finalizar feature: git flow feature finish nombre-feature"
echo "  - Crear release: git flow release start 1.0.0"
echo "  - Finalizar release: git flow release finish 1.0.0"
echo "  - Crear hotfix: git flow hotfix start nombre-hotfix"
echo "  - Finalizar hotfix: git flow hotfix finish nombre-hotfix"
echo ""
echo "🌿 Rama actual: $(git branch --show-current)" 