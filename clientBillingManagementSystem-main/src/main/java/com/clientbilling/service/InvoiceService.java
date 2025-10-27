package com.clientbilling.service;

import com.clientbilling.model.Invoice;
import com.clientbilling.model.Project;
import com.clientbilling.repository.InvoiceRepository;
import com.clientbilling.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProjectRepository projectRepository; // ✅ Add this

    @Transactional
    public ByteArrayInputStream generateInvoicePDF(Invoice invoice) {
        try {
            // ✅ Fetch the project from DB
        	Optional<Project> projectOpt = projectRepository.findByProjectIdNo(invoice.getProjectIdNo());


            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                invoice.setProject(project);

                // Calculate net amount
                if (invoice.getTotalHours() != null) {
                    invoice.setNetAmount(invoice.getTotalHours() * project.getBillingRate());
                }
            }

            // ✅ Generate PDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("INVOICE").setBold().setFontSize(18));
            document.add(new Paragraph("Invoice ID: " + invoice.getInvoiceId()));
            document.add(new Paragraph("Client ID: " + invoice.getClientIdNo()));
            document.add(new Paragraph("Project ID: " + invoice.getProjectIdNo()));
            document.add(new Paragraph("Month: " + invoice.getMonth()));
            document.add(new Paragraph("Total Hours: " + invoice.getTotalHours()));
            document.add(new Paragraph("Net Amount: $" + invoice.getNetAmount()));

            document.close();

            // ✅ Save invoice to DB
            invoiceRepository.save(invoice);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}
