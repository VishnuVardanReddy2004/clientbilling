package com.clientbilling.dto;

public class OrderRequest {
    private int amount;         // in paise
    private String currency;
    private String receiptId;

    // Getters and setters
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getReceiptId() { return receiptId; }
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
	@Override
	public String toString() {
		return "OrderRequest [amount=" + amount + ", currency=" + currency + ", receiptId=" + receiptId + "]";
	}
}