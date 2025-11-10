# 🚀 Script de Poblado de Base de Datos DistrIA

Script Python **100% standalone** para poblar la base de datos de GestorAPI con datos de prueba masivos.

**⚠️ IMPORTANTE:** Este script NO modifica el código de GestorAPI, solo inserta datos en PostgreSQL.

---

## 📋 Requisitos

- Python 3.8 o superior
- Acceso a internet (para conectar a PostgreSQL en Render)

---

## 🔧 Instalación

1. **Instalar dependencias:**

```bash
cd scripts
pip install -r requirements.txt
```

O si usas `pip3`:

```bash
pip3 install -r requirements.txt
```

---

## 🚀 Uso

### Opción 1: Ejecutar directamente

```bash
python seed_database.py
```

O:

```bash
python3 seed_database.py
```

### Opción 2: Dar permisos de ejecución (Linux/Mac)

```bash
chmod +x seed_database.py
./seed_database.py
```

---

## 📊 Datos que se Generan

El script genera automáticamente:

| Tipo de Dato | Cantidad | Descripción |
|--------------|----------|-------------|
| **Usuario ADMIN** | 1 | Gerente general con acceso completo |
| **Repartidores** | 20 | Usuarios con rol REPARTIDOR |
| **Clientes** | 1,500 | Clientes con ubicación GPS en Santa Cruz |
| **Productos** | 500 | Productos con SKU, precio y descripción |
| **Inventarios** | 500 | Registro de stock para cada producto |
| **Pedidos** | 1,000 | Pedidos con items (1-5 productos c/u) |
| **Rutas** | 200 | Rutas de entrega asignadas a repartidores |

**Total: ~3,720 registros principales + items de pedidos**

---

## 🔐 Credenciales de Acceso

Después de ejecutar el script, puedes acceder con:

### 👤 Usuario ADMIN (Gerente)
- **Email:** `admin@distria.com`
- **Password:** `admin123`
- **Rol:** ADMIN (acceso completo)

### 🚚 Repartidores
- **Email:** `repartidor1@distria.com` hasta `repartidor20@distria.com`
- **Password:** `repartidor123` (misma para todos)
- **Rol:** REPARTIDOR (acceso limitado)

---

## 🌍 Ubicaciones GPS

Todos los clientes y la empresa tienen coordenadas GPS **reales** en:

📍 **Santa Cruz de la Sierra, Bolivia**
- Centro: `-17.783327, -63.182140`
- Radio: ~10 km

---

## ⚙️ Configuración

Si necesitas cambiar las cantidades de datos, edita estas constantes en `seed_database.py`:

```python
CANTIDAD_REPARTIDORES = 20
CANTIDAD_CLIENTES = 1500
CANTIDAD_PRODUCTOS = 500
CANTIDAD_PEDIDOS = 1000
CANTIDAD_RUTAS = 200
```

---

## 🗑️ Limpiar Datos

El script **automáticamente limpia** todos los datos existentes antes de insertar nuevos.

Si quieres **conservar** datos existentes, comenta estas líneas en `seed_database.py`:

```python
# COMENTAR ESTA SECCIÓN PARA NO LIMPIAR
cursor.execute("TRUNCATE TABLE ruta_pedidos CASCADE")
cursor.execute("TRUNCATE TABLE rutas_entrega CASCADE")
# ... resto de líneas TRUNCATE ...
```

---

## 🔄 Re-ejecutar el Script

Puedes ejecutar el script **cuantas veces quieras**. Cada vez:

1. ✅ Limpia los datos existentes
2. ✅ Genera nuevos datos aleatorios
3. ✅ Mantiene las mismas credenciales de acceso

---

## ⏱️ Tiempo de Ejecución

- **Tiempo estimado:** 30-60 segundos
- Depende de la velocidad de conexión a Render

---

## 🐛 Solución de Problemas

### Error: "No module named 'psycopg2'"

```bash
pip install psycopg2-binary
```

### Error: "No module named 'bcrypt'"

```bash
pip install bcrypt
```

### Error: "No module named 'faker'"

```bash
pip install Faker
```

### Error de conexión a PostgreSQL

Verifica que la URL de la base de datos en `seed_database.py` sea correcta:

```python
DB_CONFIG = {
    'host': 'dpg-d47jqnshg0os73fo6460-a.oregon-postgres.render.com',
    'port': 5432,
    'database': 'gestorapi_ge18',
    'user': 'admin',
    'password': 'NDFvY7PuVaE0KzlwUZbSD4W87afRKM62'
}
```

---

## 📝 Notas Importantes

1. ✅ **No toca el código de GestorAPI** - Es completamente independiente
2. ✅ **Usa BCrypt** - Passwords hasheados igual que Spring Security
3. ✅ **Datos realistas** - Usa librería Faker para nombres, direcciones, etc.
4. ✅ **Coordenadas GPS reales** - Centradas en Santa Cruz de la Sierra
5. ✅ **Respeta integridad referencial** - Inserta en orden correcto

---

## 🎯 Próximos Pasos

Después de ejecutar el script:

1. ✅ Verifica la conexión abriendo GraphiQL: `http://localhost:8080/graphiql`
2. ✅ Inicia sesión con las credenciales de ADMIN
3. ✅ Explora los datos generados en el dashboard

---

## 📧 Soporte

Si tienes problemas, verifica:
- Que las dependencias estén instaladas
- Que tengas conexión a internet
- Que la base de datos en Render esté activa

---

**🎉 ¡Disfruta probando DistrIA con datos realistas!**
