package com.clientbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.Client;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientIdNo(String clientIdNo);
    Optional<Client> findByUsername(String username);
	Optional<Client> findByEmail(String email);
  }
