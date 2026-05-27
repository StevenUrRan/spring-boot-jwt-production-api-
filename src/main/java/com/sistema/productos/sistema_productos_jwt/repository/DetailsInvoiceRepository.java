package com.sistema.productos.sistema_productos_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistema.productos.sistema_productos_jwt.entity.DetailsInvoice;

@Repository
public interface DetailsInvoiceRepository extends JpaRepository<DetailsInvoice, Long> {

}
