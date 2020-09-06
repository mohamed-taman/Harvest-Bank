package org.siriusxi.blueharvest.bank.as;

import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.as.persistence.AccountRepository;
import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.siriusxi.blueharvest.bank.common.api.composite.account.AccountType;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryIntegrationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void whenFindByCustomerId_thenReturnAccount() {
        // given
        AccountEntity trx = new AccountEntity(99, new BigDecimal("100.20"));
        accountRepository.save(trx);

        // when
        AccountEntity found =
                accountRepository.findByCustomerId(trx.getCustomerId()).iterator().next();

        // then
        assertThat(found.getId()).isEqualTo(1);
        assertThat(found.getCustomerId()).isEqualTo(99);
        assertThat(found.getBalance()).isEqualTo(new BigDecimal("100.20"));
        assertThat(found.getType()).isEqualTo(AccountType.CURRENT);
    }
}
