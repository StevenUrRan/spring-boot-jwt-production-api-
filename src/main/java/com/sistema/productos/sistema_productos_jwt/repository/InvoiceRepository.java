package com.sistema.productos.sistema_productos_jwt.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistema.productos.sistema_productos_jwt.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE i.auth.createAt BETWEEN :start AND :end ORDER BY i.auth.createAt DESC")
    List<Invoice> findByDateBetween(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}
