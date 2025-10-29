package com.sw2.gestorapi.domain.repositories;

import com.sw2.gestorapi.domain.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    /**
     * Busca productos por empresa
     */
    List<Producto> findByEmpresaIdAndActiveTrue(UUID empresaId);

    /**
     * Busca un producto por SKU
     */
    Optional<Producto> findBySku(String sku);

    /**
     * Busca un producto por SKU y empresa
     */
    Optional<Producto> findBySkuAndEmpresaId(String sku, UUID empresaId);

    /**
     * Busca productos por categoría
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "LOWER(p.categoria) = LOWER(:categoria) AND p.active = true")
    List<Producto> findByEmpresaIdAndCategoria(@Param("empresaId") UUID empresaId, 
                                               @Param("categoria") String categoria);

    /**
     * Busca productos con stock bajo (stock actual <= stock mínimo)
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "p.stockActual <= p.stockMinimo AND p.active = true")
    List<Producto> findProductosConStockBajo(@Param("empresaId") UUID empresaId);

    /**
     * Busca productos por nombre (búsqueda parcial)
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.active = true")
    List<Producto> findByEmpresaIdAndNombreContaining(@Param("empresaId") UUID empresaId, 
                                                      @Param("nombre") String nombre);

    /**
     * Busca productos por rango de precios
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "p.precio BETWEEN :precioMin AND :precioMax AND p.active = true")
    List<Producto> findByEmpresaIdAndPrecioBetween(@Param("empresaId") UUID empresaId,
                                                   @Param("precioMin") BigDecimal precioMin,
                                                   @Param("precioMax") BigDecimal precioMax);

    /**
     * Busca productos que requieren refrigeración
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "p.requiereRefrigeracion = true AND p.active = true")
    List<Producto> findProductosQueRequierenRefrigeracion(@Param("empresaId") UUID empresaId);

    /**
     * Busca productos frágiles
     */
    @Query("SELECT p FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "p.esFragil = true AND p.active = true")
    List<Producto> findProductosFragiles(@Param("empresaId") UUID empresaId);

    /**
     * Cuenta productos por empresa
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.empresa.id = :empresaId AND p.active = true")
    Long countByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Obtiene las categorías únicas de productos de una empresa
     */
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.empresa.id = :empresaId AND " +
           "p.categoria IS NOT NULL AND p.active = true ORDER BY p.categoria")
    List<String> findDistinctCategoriasByEmpresaId(@Param("empresaId") UUID empresaId);

    /**
     * Verifica si existe un producto con el SKU dado en la empresa
     */
    boolean existsBySkuAndEmpresaId(String sku, UUID empresaId);
}
