package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayPaymentId;   // Razorpay payment ID
    private String razorpayOrderId;     // Razorpay order ID
    private String razorpaySignature;   // For verification
    private String status;              // Payment status (e.g., "captured", "failed")
    private Double amount;              // Payment amount in main currency unit (₹)

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;            // Linked invoice

    private Long paymentTimestamp;     // optional: timestamp of payment

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRazorpayPaymentId() {
		return razorpayPaymentId;
	}

	public void setRazorpayPaymentId(String razorpayPaymentId) {
		this.razorpayPaymentId = razorpayPaymentId;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getRazorpaySignature() {
		return razorpaySignature;
	}

	public void setRazorpaySignature(String razorpaySignature) {
		this.razorpaySignature = razorpaySignature;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Long getPaymentTimestamp() {
		return paymentTimestamp;
	}

	public void setPaymentTimestamp(Long paymentTimestamp) {
		this.paymentTimestamp = paymentTimestamp;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", razorpayPaymentId=" + razorpayPaymentId + ", razorpayOrderId=" + razorpayOrderId
				+ ", razorpaySignature=" + razorpaySignature + ", status=" + status + ", amount=" + amount
				+ ", invoice=" + invoice + ", paymentTimestamp=" + paymentTimestamp + "]";
	}
}