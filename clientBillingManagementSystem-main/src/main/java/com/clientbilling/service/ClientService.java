package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    // Register client with email validation
    public Client registerClient(Client client) {

        // ✅ Validate email
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // ✅ Optional: check uniqueness
        Optional<Client> existing = clientRepository.findByEmail(client.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Attach existing Admin
        if (client.getAdmin() != null && client.getAdmin().getId() != null) {
            Admin existingAdmin = adminRepository.findById(client.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            client.setAdmin(existingAdmin);

            // ✅ Maintain bidirectional relationship
            existingAdmin.getClients().add(client);
        }

        return clientRepository.save(client);
    }

    // Get client by ID
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    // Get all clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Delete client
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
