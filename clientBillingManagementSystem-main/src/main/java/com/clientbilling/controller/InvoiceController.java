package com.clientbilling.controller;

import com.clientbilling.model.Invoice;
import com.clientbilling.security.SecurityUtil;
import com.clientbilling.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoice(@RequestBody Invoice invoice) {
        try {
            String role = securityUtil.getCurrentUserRoles().stream().findFirst().orElse("");
            if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD")) {
                return ResponseEntity.status(403).body("Access Denied");
            }

            var pdfStream = invoiceService.generateInvoicePDF(invoice);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=invoice.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating invoice PDF: " + e.getMessage());
        }
    }
}
