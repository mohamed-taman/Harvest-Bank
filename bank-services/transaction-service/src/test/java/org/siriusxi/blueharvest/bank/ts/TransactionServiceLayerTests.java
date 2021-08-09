package org.siriusxi.blueharvest.bank.ts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.ts.persistence.TransactionRepository;
import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.siriusxi.blueharvest.bank.ts.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
abstract class TransactionServiceLayerTests {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() throws Exception {

        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void createTransactions() {
        TransactionEntity trx1 = new TransactionEntity(1, new BigDecimal("100.20"));

        transactionService.createTransaction(trx1);
        transactionService.createTransaction(trx1);

        verify(transactionRepository, times(2)).save(trx1);

    }

    @Test
    void whenAddNewTransactions_thenTransactionsShouldBeFound() {
        //Given
        when(transactionRepository.findByAccountId(1))
            .thenReturn(
                List.of(new TransactionEntity(1, new BigDecimal("100.20")),
                    new TransactionEntity(1, new BigDecimal("200.20"))));

        //when
        List<Transaction> found = transactionService.getTransactions(1);

        //Then
        assertThat(found.size()).isEqualTo(2);
        assertThat(found.get(0).amount()).isEqualTo(new BigDecimal("100.20"));
        verify(transactionRepository, times(1)).findByAccountId(1);
    }
}
