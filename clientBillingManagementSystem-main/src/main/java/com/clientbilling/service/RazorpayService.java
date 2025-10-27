package com.clientbilling.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private RazorpayClient razorpayClient;

    public RazorpayService(@Value("${razorpay.keyId}") String keyId,
                          @Value("${razorpay.keySecret}") String keySecret) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    /**
     * Create an order in Razorpay system
     * @param amountInPaise amount in smallest currency unit (paise for INR)
     * @param currency currency code e.g. "INR"
     * @param receiptId unique string for receipt/order tracking
     * @return order JSON object from Razorpay API
     * @throws RazorpayException if any Razorpay call fails
     */
    public JSONObject createOrder(int amountInPaise, String currency, String receiptId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receiptId);
        orderRequest.put("payment_capture", 1); // auto capture payment

        Order order = razorpayClient.orders.create(orderRequest); // Note lowercase 'orders'
        return order.toJson();
    }
}