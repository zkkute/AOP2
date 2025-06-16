package service;

import org.example.DTO.IncomingTransactionDto;
import org.example.entity.Account;
import org.example.enums.AccountStatus;
import org.example.enums.TransactionStatus;
import org.example.repository.AccountRepository;
import org.example.repository.ClientRepository;
import org.example.repository.TransactionRepository;
import org.example.config.TransactionConfig;
import org.example.service.TransactionProcessingService;
import org.example.starter.service.kafka.KafkaProducerService;
import org.example.client.ClientStatusClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionProcessingServiceTest {

    @InjectMocks
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ClientStatusClient clientStatusClient;

    @Mock
    private TransactionConfig transactionConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessIncomingTransaction_AccountNotFound() {
        IncomingTransactionDto dto = new IncomingTransactionDto();
        dto.setAccountId("acc1");
        dto.setClientId("client1");
        dto.setTransactionId("tx1");
        dto.setAmount(100.0);
        dto.setTimestamp(System.currentTimeMillis());

        when(accountRepository.findByAccountId(dto.getAccountId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionProcessingService.processIncomingTransaction(dto));
    }

    @Test
    void testProcessIncomingTransaction_ClientBlocked() {
        IncomingTransactionDto dto = new IncomingTransactionDto();
        dto.setAccountId("acc1");
        dto.setClientId("client1");
        dto.setTransactionId("tx1");
        dto.setAmount(100.0);
        dto.setTimestamp(System.currentTimeMillis());

        Account account = new Account();
        account.setAccountId("acc1");
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByAccountId(dto.getAccountId())).thenReturn(Optional.of(account));
        when(clientRepository.findByClientId(dto.getClientId())).thenReturn(Optional.empty());
        when(clientStatusClient.getClientStatus(dto.getClientId(), dto.getAccountId())).thenReturn(AccountStatus.BLOCKED.name());

        transactionProcessingService.processIncomingTransaction(dto);

        verify(accountRepository, times(1)).save(argThat(acc -> acc.getStatus() == AccountStatus.BLOCKED));
    }

    @Test
    void testProcessIncomingTransaction_ExceedsMaxRejected() {
        IncomingTransactionDto dto = new IncomingTransactionDto();
        dto.setAccountId("acc1");
        dto.setClientId("client1");
        dto.setTransactionId("tx1");
        dto.setAmount(100.0);
        dto.setTimestamp(System.currentTimeMillis());

        Account account = new Account();
        account.setAccountId("acc1");
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByAccountId(dto.getAccountId())).thenReturn(Optional.of(account));
        when(clientRepository.findByClientId(dto.getClientId())).thenReturn(Optional.empty());
        when(clientStatusClient.getClientStatus(dto.getClientId(), dto.getAccountId())).thenReturn(AccountStatus.OPEN.name());

        when(transactionConfig.getMaxRejectedBeforeArrested()).thenReturn(3);

        List<org.example.entity.Transaction> rejectedList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            org.example.entity.Transaction tx = new org.example.entity.Transaction();
            tx.setStatus(TransactionStatus.REJECTED);
            rejectedList.add(tx);
        }

        when(transactionRepository.findByAccountIdAndStatus(dto.getAccountId(), TransactionStatus.REJECTED))
                .thenReturn(rejectedList);

        transactionProcessingService.processIncomingTransaction(dto);

        verify(accountRepository, times(1)).save(argThat(acc -> acc.getStatus() == AccountStatus.ARRESTED));
    }
}