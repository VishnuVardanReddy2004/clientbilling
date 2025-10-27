package com.clientbilling.repository;

import com.clientbilling.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payments by invoice
    List<Payment> findByInvoiceId(Long invoiceId);

    // Find payment by razorpayPaymentId
    Payment findByRazorpayPaymentId(String razorpayPaymentId);
}