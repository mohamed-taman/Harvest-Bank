package org.siriusxi.blueharvest.bank.ts;

import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType;
import org.siriusxi.blueharvest.bank.ts.persistence.TransactionRepository;
import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryLayerTests {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void whenFindByAccountId_thenReturnTransaction() {
        // Given
        TransactionEntity trx = new TransactionEntity(1, new BigDecimal("100.20"));
        transactionRepository.save(trx);

        // When
        TransactionEntity found =
                transactionRepository.findByAccountId(trx.getAccountId()).iterator().next();

        // Then
        assertThat(found.getId()).isEqualTo(1);
        assertThat(found.getAccountId()).isEqualTo(1);
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("100.20"));
        assertThat(found.getType()).isEqualTo(TransactionType.CREDIT);
    }

}
