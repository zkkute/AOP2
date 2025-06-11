package org.example.service2.repository;

import org.example.service2.entity.Service2Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Service2ClientRepository extends JpaRepository<Service2Client, Long> {
    Optional<Service2Client> findByClientId(String clientId);
}