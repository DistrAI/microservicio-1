#!/usr/bin/env python3
"""
Script para poblar la base de datos de DistrIA con datos de prueba
100% STANDALONE - No modifica el código de GestorAPI

Autor: DistrIA Team
Fecha: 2024
"""

import random
import sys
from datetime import datetime, timedelta
from decimal import Decimal

import bcrypt
import psycopg2
from faker import Faker

# ============================================================================
# CONFIGURACIÓN DE BASE DE DATOS
# ============================================================================
DB_CONFIG = {
    'host': 'dpg-d49a9fk9c44c73bilt60-a.oregon-postgres.render.com',
    'port': 5432,
    'database': 'gestorapi_ij3r',
    'user': 'admin',
    'password': 'GimjVfMKs8ca2LSryY24otdIMJWl38W2'
}

# ============================================================================
# CONFIGURACIÓN DE DATOS A GENERAR (MÁS REALISTA)
# ============================================================================
CANTIDAD_REPARTIDORES = 25
CANTIDAD_CLIENTES = 800
CANTIDAD_PRODUCTOS = 600
CANTIDAD_PEDIDOS = 3500  # Más pedidos que clientes (4-5 pedidos por cliente promedio)
CANTIDAD_RUTAS = 300

# ============================================================================
# COORDENADAS GPS EXACTAS DE SANTA CRUZ DE LA SIERRA, BOLIVIA
# ============================================================================
# Centro: Plaza 24 de Septiembre (Corazón de Santa Cruz)
SANTA_CRUZ_CENTER = {
    'lat': -17.783444,  # Latitud exacta del centro de Santa Cruz
    'lng': -63.182127   # Longitud exacta del centro de Santa Cruz
}

# Radio de cobertura urbana (aprox 15km - cubre toda el área metropolitana)
GPS_RADIUS = 0.135  # Equivale a ~15km de radio

# Zonas específicas de Santa Cruz de la Sierra - Coordenadas reales y precisas
ZONAS_SANTA_CRUZ = [
    # Centro histórico y primer anillo
    {'nombre': 'Centro Histórico', 'lat': -17.783327, 'lng': -63.182140, 'radio': 0.008},
    {'nombre': 'Equipetrol Norte', 'lat': -17.770000, 'lng': -63.180000, 'radio': 0.012},
    {'nombre': 'Las Palmas', 'lat': -17.775000, 'lng': -63.195000, 'radio': 0.010},
    {'nombre': 'Barrio Hamacas', 'lat': -17.790000, 'lng': -63.175000, 'radio': 0.008},
    
    # Segundo anillo - Zonas residenciales
    {'nombre': 'Plan 3000', 'lat': -17.745000, 'lng': -63.165000, 'radio': 0.020},
    {'nombre': 'Villa 1ro de Mayo', 'lat': -17.815000, 'lng': -63.148000, 'radio': 0.015},
    {'nombre': 'Pampa de la Isla', 'lat': -17.748000, 'lng': -63.198000, 'radio': 0.018},
    {'nombre': 'Barrio Petrolero', 'lat': -17.795000, 'lng': -63.190000, 'radio': 0.012},
    {'nombre': 'Villa Olímpica', 'lat': -17.760000, 'lng': -63.175000, 'radio': 0.010},
    
    # Tercer anillo - Zonas comerciales y residenciales
    {'nombre': 'Av. Santos Dumont', 'lat': -17.798000, 'lng': -63.165000, 'radio': 0.015},
    {'nombre': 'Radial 10', 'lat': -17.765000, 'lng': -63.130000, 'radio': 0.020},
    {'nombre': 'Radial 13', 'lat': -17.815000, 'lng': -63.200000, 'radio': 0.018},
    {'nombre': 'Radial 17½', 'lat': -17.800000, 'lng': -63.210000, 'radio': 0.015},
    {'nombre': 'Radial 26', 'lat': -17.740000, 'lng': -63.180000, 'radio': 0.017},
    {'nombre': 'Av. Alemana', 'lat': -17.785000, 'lng': -63.145000, 'radio': 0.012},
    
    # Cuarto anillo y zonas periféricas
    {'nombre': 'Villa Primero de Mayo Norte', 'lat': -17.730000, 'lng': -63.160000, 'radio': 0.025},
    {'nombre': 'Satélite Norte', 'lat': -17.720000, 'lng': -63.175000, 'radio': 0.020},
    {'nombre': 'Plan Tres Mil Sur', 'lat': -17.820000, 'lng': -63.155000, 'radio': 0.022},
    {'nombre': 'Zona Sur (Mutualista)', 'lat': -17.830000, 'lng': -63.170000, 'radio': 0.018},
    {'nombre': 'Zona Este (Palmasola)', 'lat': -17.780000, 'lng': -63.110000, 'radio': 0.025},
    {'nombre': 'Zona Oeste (El Bajío)', 'lat': -17.785000, 'lng': -63.220000, 'radio': 0.020},
    
    # Zonas comerciales importantes
    {'nombre': 'Ventura Mall', 'lat': -17.800500, 'lng': -63.156000, 'radio': 0.008},
    {'nombre': 'Las Brisas', 'lat': -17.770500, 'lng': -63.165000, 'radio': 0.010},
    {'nombre': 'Barrio Urbari', 'lat': -17.792000, 'lng': -63.200000, 'radio': 0.012},
]

# ============================================================================
# INICIALIZAR FAKER
# ============================================================================
fake = Faker('es_ES')  # Español de España (nombres latinos)
Faker.seed(42)  # Para reproducibilidad
random.seed(42)


# ============================================================================
# FUNCIONES AUXILIARES
# ============================================================================

def hash_password(password: str) -> str:
    """Hashea una contraseña usando BCrypt (compatible con Spring Security)"""
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')


def generar_coordenadas_santa_cruz():
    """Genera coordenadas GPS aleatorias dentro de zonas específicas de Santa Cruz"""
    # Seleccionar una zona aleatoria de Santa Cruz (80% zonas específicas, 20% general)
    if random.random() < 0.8:
        # Usar zona específica
        zona = random.choice(ZONAS_SANTA_CRUZ)
        centro_lat = zona['lat']
        centro_lng = zona['lng']
        radio = zona['radio']
    else:
        # Usar área general
        centro_lat = SANTA_CRUZ_CENTER['lat']
        centro_lng = SANTA_CRUZ_CENTER['lng']
        radio = GPS_RADIUS
    
    # Generar offset aleatorio dentro del radio de la zona
    lat_offset = random.uniform(-radio, radio)
    lng_offset = random.uniform(-radio, radio)
    
    lat = centro_lat + lat_offset
    lng = centro_lng + lng_offset
    
    # Asegurar que las coordenadas estén dentro de Santa Cruz
    lat = max(-17.900000, min(-17.650000, lat))  # Límites de Santa Cruz
    lng = max(-63.300000, min(-63.050000, lng))
    
    return round(lat, 8), round(lng, 8)


def generar_sku():
    """Genera un SKU único para productos"""
    return f"PRD-{random.randint(10000, 99999)}-{fake.random_uppercase_letter()}{fake.random_uppercase_letter()}"


def generar_precio():
    """Genera un precio aleatorio realista"""
    return round(random.uniform(5.0, 500.0), 2)


def generar_fecha_reciente(dias_atras=90):
    """Genera una fecha aleatoria en los últimos N días"""
    dias_random = random.randint(0, dias_atras)
    return datetime.now() - timedelta(days=dias_random)


def generar_direccion_santa_cruz():
    """Genera una dirección realista de Santa Cruz de la Sierra"""
    
    # Calles principales de Santa Cruz
    calles_principales = [
        "Av. Monseñor Rivero", "Av. Cristo Redentor", "Av. Santos Dumont", "Av. Alemana",
        "Av. Banzer", "Av. Roca y Coronado", "Av. Irala", "Av. Cañoto", "Av. Beni",
        "Av. Paraguá", "Av. Grigotá", "Av. San Martín", "Av. Mutualista", "Av. Piraí"
    ]
    
    # Radiales de Santa Cruz
    radiales = [
        "Radial 10", "Radial 13", "Radial 17½", "Radial 19", "Radial 26", "Radial 27"
    ]
    
    # Anillos de Santa Cruz
    anillos = [
        "1er Anillo", "2do Anillo", "3er Anillo", "4to Anillo", "5to Anillo"
    ]
    
    # Calles comunes
    calles_comunes = [
        "Calle Sucre", "Calle Bolívar", "Calle Junín", "Calle Potosí", "Calle La Paz",
        "Calle Cochabamba", "Calle Tarija", "Calle Beni", "Calle Pando", "Calle Oruro",
        "Calle 21 de Mayo", "Calle Warnes", "Calle Velasco", "Calle Ballivián"
    ]
    
    # Barrios específicos
    barrios = [
        "Plan 3000", "Villa 1ro de Mayo", "Equipetrol", "Las Palmas", "Pampa de la Isla",
        "Barrio Hamacas", "Villa Olímpica", "Barrio Petrolero", "Las Brisas", "Urbari",
        "Satélite Norte", "Plan Tres Mil", "El Bajío", "Palmasola"
    ]
    
    # Generar dirección aleatoria
    tipo_direccion = random.choice(['avenida', 'radial', 'anillo', 'calle', 'barrio'])
    
    if tipo_direccion == 'avenida':
        calle = random.choice(calles_principales)
        numero = random.randint(100, 9999)
        return f"{calle} #{numero}"
    
    elif tipo_direccion == 'radial':
        radial = random.choice(radiales)
        numero = random.randint(50, 500)
        return f"{radial} Km {numero/100:.1f}"
    
    elif tipo_direccion == 'anillo':
        anillo = random.choice(anillos)
        numero = random.randint(100, 2000)
        return f"{anillo} #{numero}"
    
    elif tipo_direccion == 'calle':
        calle = random.choice(calles_comunes)
        numero = random.randint(50, 800)
        barrio = random.choice(barrios)
        return f"{calle} #{numero}, {barrio}"
    
    else:  # barrio
        barrio = random.choice(barrios)
        manzana = random.choice(['A', 'B', 'C', 'D', 'E', 'F'])
        lote = random.randint(1, 30)
        return f"Barrio {barrio}, Mz. {manzana} Lote {lote}"


# ============================================================================
# FUNCIÓN PRINCIPAL DE POBLADO
# ============================================================================

def poblar_base_datos():
    """Función principal que puebla toda la base de datos"""
    
    print("=" * 70)
    print("🚀 INICIANDO POBLADO DE BASE DE DATOS DE DistrIA")
    print("=" * 70)
    
    try:
        # Conectar a la base de datos
        print("\n📡 Conectando a PostgreSQL en Render...")
        conn = psycopg2.connect(**DB_CONFIG)
        conn.autocommit = False
        cursor = conn.cursor()
        print("✅ Conexión exitosa\n")
        
        # ====================================================================
        # 1. LIMPIAR DATOS EXISTENTES (OPCIONAL - COMENTAR SI NO QUIERES)
        # ====================================================================
        print("🗑️  Limpiando datos existentes...")
        cursor.execute("TRUNCATE TABLE ruta_pedidos CASCADE")
        cursor.execute("TRUNCATE TABLE rutas_entrega CASCADE")
        cursor.execute("TRUNCATE TABLE items_pedido CASCADE")
        cursor.execute("TRUNCATE TABLE pedidos CASCADE")
        cursor.execute("TRUNCATE TABLE movimientos_inventario CASCADE")
        cursor.execute("TRUNCATE TABLE inventarios CASCADE")
        cursor.execute("TRUNCATE TABLE productos CASCADE")
        cursor.execute("TRUNCATE TABLE clientes CASCADE")
        cursor.execute("TRUNCATE TABLE usuarios CASCADE")
        conn.commit()
        print("✅ Datos limpiados\n")
        
        # ====================================================================
        # 2. CREAR USUARIO ADMIN (GERENTE)
        # ====================================================================
        print("👤 Creando usuario ADMIN (Gerente)...")
        admin_password = hash_password("admin123")  # Contraseña: admin123
        lat_empresa, lng_empresa = generar_coordenadas_santa_cruz()
        
        cursor.execute("""
            INSERT INTO usuarios (
                nombre_completo, email, password, rol, telefono,
                direccion_empresa, latitud_empresa, longitud_empresa, nombre_empresa,
                activo, fecha_creacion, fecha_actualizacion
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
            ) RETURNING id
        """, (
            "Juan Carlos Pérez - Gerente General",
            "admin@distria.com",
            admin_password,
            "ADMIN",
            "+591 3 123-4567",
            "Av. Monseñor Rivero #123, Santa Cruz de la Sierra",
            lat_empresa,
            lng_empresa,
            "DistrIA Logistics Bolivia",
            True,
            datetime.now(),
            datetime.now()
        ))
        admin_id = cursor.fetchone()[0]
        conn.commit()
        print(f"✅ Usuario ADMIN creado (ID: {admin_id})")
        print(f"   📧 Email: admin@distria.com")
        print(f"   🔑 Password: admin123\n")
        
        # ====================================================================
        # 3. CREAR REPARTIDORES
        # ====================================================================
        print(f"🚚 Creando {CANTIDAD_REPARTIDORES} repartidores...")
        repartidor_ids = []
        repartidor_password = hash_password("repartidor123")  # Misma contraseña para todos
        
        for i in range(CANTIDAD_REPARTIDORES):
            cursor.execute("""
                INSERT INTO usuarios (
                    nombre_completo, email, password, rol, telefono,
                    activo, fecha_creacion, fecha_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s
                ) RETURNING id
            """, (
                fake.name(),
                f"repartidor{i+1}@distria.com",
                repartidor_password,
                "REPARTIDOR",
                fake.phone_number(),
                True,
                datetime.now(),
                datetime.now()
            ))
            repartidor_ids.append(cursor.fetchone()[0])
        
        conn.commit()
        print(f"✅ {len(repartidor_ids)} repartidores creados")
        print(f"   🔑 Password para todos: repartidor123\n")
        
        # ====================================================================
        # 4. CREAR CLIENTES
        # ====================================================================
        print(f"👥 Creando {CANTIDAD_CLIENTES} clientes...")
        cliente_ids = []
        cliente_password = hash_password("cliente123")  # Misma contraseña para todos
        
        for i in range(CANTIDAD_CLIENTES):
            lat, lng = generar_coordenadas_santa_cruz()
            cursor.execute("""
                INSERT INTO clientes (
                    nombre, email, password, telefono, direccion,
                    latitud_cliente, longitud_cliente, referencia_direccion,
                    activo, fecha_creacion, fecha_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
                ) RETURNING id
            """, (
                fake.name(),
                f"cliente{i+1}@distria.com",  # Emails similares como repartidores
                cliente_password,
                f"+591 {random.randint(6,7)}{random.randint(1000000,9999999)}",  # Números bolivianos
                generar_direccion_santa_cruz(),  # Direcciones realistas de Santa Cruz
                lat,
                lng,
                f"Referencia: {fake.sentence(nb_words=6)}",  # Referencias más realistas
                True,
                generar_fecha_reciente(180),
                datetime.now()
            ))
            cliente_ids.append(cursor.fetchone()[0])
            
            if (i + 1) % 200 == 0:  # Actualizado para mostrar progreso cada 200
                print(f"   📝 {i + 1}/{CANTIDAD_CLIENTES} clientes creados...")
                conn.commit()
        
        conn.commit()
        print(f"✅ {len(cliente_ids)} clientes creados")
        print(f"   🔑 Password para todos: cliente123\n")
        
        # ====================================================================
        # 5. CREAR PRODUCTOS
        # ====================================================================
        print(f"📦 Creando {CANTIDAD_PRODUCTOS} productos...")
        producto_ids = []
        
        categorias_productos = [
            "Electrónica", "Alimentos", "Bebidas", "Ropa", "Calzado",
            "Hogar", "Ferretería", "Juguetes", "Deportes", "Libros"
        ]
        
        for i in range(CANTIDAD_PRODUCTOS):
            categoria = random.choice(categorias_productos)
            cursor.execute("""
                INSERT INTO productos (
                    nombre, sku, descripcion, precio,
                    activo, fecha_creacion, fecha_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s
                ) RETURNING id
            """, (
                f"{categoria} {fake.word().capitalize()} {fake.color_name()}",
                generar_sku(),
                fake.text(max_nb_chars=200),
                generar_precio(),
                True,
                generar_fecha_reciente(365),
                datetime.now()
            ))
            producto_ids.append(cursor.fetchone()[0])
            
            if (i + 1) % 100 == 0:
                print(f"   📝 {i + 1}/{CANTIDAD_PRODUCTOS} productos creados...")
                conn.commit()
        
        conn.commit()
        print(f"✅ {len(producto_ids)} productos creados\n")
        
        # ====================================================================
        # 6. CREAR INVENTARIOS
        # ====================================================================
        print(f"📊 Creando {len(producto_ids)} registros de inventario...")
        
        ubicaciones_bodega = [
            "Estante A-1", "Estante A-2", "Estante B-1", "Estante B-2",
            "Bodega Principal", "Bodega Secundaria", "Refrigerador 1",
            "Zona de Carga", "Almacén General"
        ]
        
        for producto_id in producto_ids:
            cursor.execute("""
                INSERT INTO inventarios (
                    producto_id, cantidad, ubicacion, stock_minimo,
                    activo, fecha_creacion, fecha_ultima_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s
                )
            """, (
                producto_id,
                random.randint(10, 500),
                random.choice(ubicaciones_bodega),
                random.randint(5, 20),
                True,
                generar_fecha_reciente(365),
                datetime.now()
            ))
        
        conn.commit()
        print(f"✅ {len(producto_ids)} inventarios creados\n")
        
        # ====================================================================
        # 7. CREAR PEDIDOS E ITEMS
        # ====================================================================
        print(f"🛒 Creando {CANTIDAD_PEDIDOS} pedidos con items...")
        pedido_ids = []
        estados_pedido = ['PENDIENTE', 'EN_PROCESO', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO']
        
        for i in range(CANTIDAD_PEDIDOS):
            cliente_id = random.choice(cliente_ids)
            estado = random.choice(estados_pedido)
            fecha_pedido = generar_fecha_reciente(60)
            
            # Crear el pedido
            cursor.execute("""
                INSERT INTO pedidos (
                    cliente_id, estado, total, direccion_entrega,
                    observaciones, fecha_entrega, activo,
                    fecha_pedido, fecha_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s
                ) RETURNING id
            """, (
                cliente_id,
                estado,
                0,  # Se calculará después
                generar_direccion_santa_cruz(),  # Direcciones realistas para entregas
                fake.sentence() if random.random() > 0.7 else None,
                fecha_pedido + timedelta(days=random.randint(1, 5)) if estado in ['ENTREGADO', 'EN_CAMINO'] else None,
                True,
                fecha_pedido,
                datetime.now()
            ))
            pedido_id = cursor.fetchone()[0]
            pedido_ids.append(pedido_id)
            
            # Crear items del pedido (entre 1 y 5 productos)
            num_items = random.randint(1, 5)
            total_pedido = Decimal('0.00')
            
            for _ in range(num_items):
                producto_id = random.choice(producto_ids)
                cantidad = random.randint(1, 10)
                
                # Obtener precio del producto
                cursor.execute("SELECT precio FROM productos WHERE id = %s", (producto_id,))
                precio_unitario = cursor.fetchone()[0]
                subtotal = Decimal(str(precio_unitario)) * cantidad
                total_pedido += subtotal
                
                cursor.execute("""
                    INSERT INTO items_pedido (
                        pedido_id, producto_id, cantidad, precio_unitario, subtotal
                    ) VALUES (
                        %s, %s, %s, %s, %s
                    )
                """, (
                    pedido_id,
                    producto_id,
                    cantidad,
                    precio_unitario,
                    subtotal
                ))
            
            # Actualizar total del pedido
            cursor.execute("""
                UPDATE pedidos SET total = %s WHERE id = %s
            """, (total_pedido, pedido_id))
            
            if (i + 1) % 100 == 0:
                print(f"   📝 {i + 1}/{CANTIDAD_PEDIDOS} pedidos creados...")
                conn.commit()
        
        conn.commit()
        print(f"✅ {len(pedido_ids)} pedidos creados con items\n")
        
        # ====================================================================
        # 8. CREAR RUTAS DE ENTREGA
        # ====================================================================
        print(f"🗺️  Creando {CANTIDAD_RUTAS} rutas de entrega...")
        estados_ruta = ['PLANIFICADA', 'EN_CURSO', 'COMPLETADA', 'CANCELADA']
        
        # Filtrar pedidos que no estén CANCELADOS para asignar a rutas
        cursor.execute("""
            SELECT id FROM pedidos 
            WHERE estado IN ('PENDIENTE', 'EN_PROCESO', 'EN_CAMINO', 'ENTREGADO')
            ORDER BY RANDOM()
            LIMIT %s
        """, (CANTIDAD_RUTAS * 5,))  # 5 pedidos por ruta en promedio
        
        pedidos_disponibles = [row[0] for row in cursor.fetchall()]
        random.shuffle(pedidos_disponibles)
        
        for i in range(CANTIDAD_RUTAS):
            repartidor_id = random.choice(repartidor_ids)
            estado_ruta = random.choice(estados_ruta)
            fecha_ruta = generar_fecha_reciente(30).date()
            
            cursor.execute("""
                INSERT INTO rutas_entrega (
                    repartidor_id, estado, fecha_ruta,
                    distancia_total_km, tiempo_estimado_min,
                    activo, fecha_creacion, fecha_actualizacion
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s
                ) RETURNING id
            """, (
                repartidor_id,
                estado_ruta,
                fecha_ruta,
                round(random.uniform(10.0, 80.0), 2),
                random.randint(60, 480),
                True,
                datetime.now(),
                datetime.now()
            ))
            ruta_id = cursor.fetchone()[0]
            
            # Asignar entre 3 y 8 pedidos a esta ruta
            num_pedidos_ruta = min(random.randint(3, 8), len(pedidos_disponibles))
            pedidos_ruta = pedidos_disponibles[:num_pedidos_ruta]
            pedidos_disponibles = pedidos_disponibles[num_pedidos_ruta:]
            
            for pedido_id in pedidos_ruta:
                cursor.execute("""
                    INSERT INTO ruta_pedidos (ruta_id, pedido_id)
                    VALUES (%s, %s)
                """, (ruta_id, pedido_id))
            
            if len(pedidos_disponibles) < 3:
                break
        
        conn.commit()
        print(f"✅ {CANTIDAD_RUTAS} rutas creadas y asignadas\n")
        
        # ====================================================================
        # FINALIZACIÓN
        # ====================================================================
        print("=" * 70)
        print("✅ ¡POBLADO DE BASE DE DATOS COMPLETADO EXITOSAMENTE!")
        print("=" * 70)
        print("\n📊 RESUMEN DE DATOS GENERADOS:")
        print(f"   • 1 Usuario ADMIN (Gerente)")
        print(f"   • {len(repartidor_ids)} Repartidores")
        print(f"   • {len(cliente_ids)} Clientes")
        print(f"   • {len(producto_ids)} Productos")
        print(f"   • {len(producto_ids)} Inventarios")
        print(f"   • {len(pedido_ids)} Pedidos con items")
        print(f"   • {CANTIDAD_RUTAS} Rutas de entrega")
        print("\n🔐 CREDENCIALES DE ACCESO:")
        print("   👤 ADMIN:")
        print("      Email: admin@distria.com")
        print("      Password: admin123")
        print("\n   🚚 REPARTIDORES:")
        print(f"      Email: repartidor1@distria.com hasta repartidor{CANTIDAD_REPARTIDORES}@distria.com")
        print("      Password: repartidor123 (para todos)")
        print("\n   👥 CLIENTES:")
        print(f"      Email: cliente1@distria.com hasta cliente{CANTIDAD_CLIENTES}@distria.com")
        print("      Password: cliente123 (para todos)")
        print(f"      Promedio: {CANTIDAD_PEDIDOS/CANTIDAD_CLIENTES:.1f} pedidos por cliente")
        print("\n🌍 Ubicaciones GPS: Santa Cruz de la Sierra, Bolivia")
        print("=" * 70)
        
        cursor.close()
        conn.close()
        
    except psycopg2.Error as e:
        print(f"\n❌ Error de base de datos: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Error inesperado: {e}")
        sys.exit(1)


if __name__ == "__main__":
    poblar_base_datos()
