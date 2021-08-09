package org.siriusxi.blueharvest.bank.as;

import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.as.persistence.AccountRepository;
import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.siriusxi.blueharvest.bank.common.api.composite.account.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryLayerTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void whenFindByCustomerId_thenReturnAccounts() {
        // given
        accountRepository.saveAll(List.of(
            new AccountEntity(99, new BigDecimal("100.20")),
            new AccountEntity(99, new BigDecimal("200.20"))));

        // when
        var accounts = accountRepository.findByCustomerId(99);

        var account = accounts.get(0);

        // then
        assertThat(accounts.size()).isEqualTo(2);

        assertThat(account.getId()).isEqualTo(1);
        assertThat(account.getCustomerId()).isEqualTo(99);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("100.20"));
        assertThat(account.getType()).isEqualTo(AccountType.CURRENT);
    }
}
