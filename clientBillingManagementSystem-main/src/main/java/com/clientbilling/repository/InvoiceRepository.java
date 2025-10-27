package com.clientbilling.repository;

import com.clientbilling.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByClientId(Long clientId);
    List<Invoice> findByProjectId(Long projectId);
}
