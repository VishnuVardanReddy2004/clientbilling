package com.clientbilling.controller;

import com.clientbilling.service.RazorpayService;
import com.clientbilling.dto.OrderRequest;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            JSONObject order = razorpayService.createOrder(
                orderRequest.getAmount(),
                orderRequest.getCurrency(),
                orderRequest.getReceiptId()
            );
            return ResponseEntity.ok(order.toString());
        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body("Error creating Razorpay order: " + e.getMessage());
        }
    }
}