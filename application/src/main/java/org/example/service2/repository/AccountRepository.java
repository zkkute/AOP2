package org.example.service2.repository;

import org.example.service2.entity.Service2Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Service2Account, Long> {
    Optional<Service2Account> findByAccountId(String accountId);
    List<Service2Account> findByClientId(String clientId); // если используется
}
