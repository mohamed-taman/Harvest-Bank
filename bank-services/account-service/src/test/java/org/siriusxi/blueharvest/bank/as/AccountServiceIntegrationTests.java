package org.siriusxi.blueharvest.bank.as;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.as.integration.TransactionIntegration;
import org.siriusxi.blueharvest.bank.as.persistence.AccountRepository;
import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.siriusxi.blueharvest.bank.as.service.AccountService;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;

import static org.siriusxi.blueharvest.bank.common.api.composite.account.AccountType.CURRENT;
import static org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType.CREDIT;
import static org.siriusxi.blueharvest.bank.util.JsonUtilities.toJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = AccountServiceApplication.class)
@AutoConfigureMockMvc
class AccountServiceIntegrationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private AccountService accountService;

  @MockBean
  private TransactionIntegration transactionIntegration;

  @Autowired
  private AccountRepository accountRepository;

  @AfterEach
  public void resetDb() {
    accountRepository.deleteAll();
  }


  @Test
  void addAccountWithInitialCreditGreaterThanZero_thenReturnAccountsAndTransactionsJsonArray()
          throws Exception {
    // Given
    var account = new AccountEntity(1, new BigDecimal("90.11"));
    accountService.createAccount(account);

    given(transactionIntegration.getAccountTransactions(1))
            .willReturn(List.of(new Transaction(1, CREDIT, new BigDecimal("90.11"))));

    // When
    mvc.perform(get("/bank/api/v1/accounts?customerId=1")
            .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].balance", is(90.11)))
            .andExpect(jsonPath("$[0].customerId", is(1)))
            .andExpect(jsonPath("$[0].type", is("CURRENT")))
            .andExpect(jsonPath("$[0].transactions", hasSize(1)))
            .andExpect(jsonPath("$[0].transactions[0].accountId", is(1)))
            .andExpect(jsonPath("$[0].transactions[0].type", is("CREDIT")))
            .andExpect(jsonPath("$[0].transactions[0].amount", is(90.11)));
  }

  @Test
  void addAccountWithInitialCreditEqualToZero_thenReturnAccountJsonArray()
          throws Exception {
    // Given
    var account = new AccountEntity(1, new BigDecimal("0.0"));
    accountService.createAccount(account);

    given(transactionIntegration.getAccountTransactions(1))
            .willReturn(emptyList());

    // When
    mvc.perform(get("/bank/api/v1/accounts?customerId=1")
            .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].balance", is(0.0)))
            .andExpect(jsonPath("$[0].customerId", is(1)))
            .andExpect(jsonPath("$[0].type", is("CURRENT")))
            .andExpect(jsonPath("$[0].transactions", hasSize(0)));
  }

  @Test
  void addAccountWithInitialCreditGreaterThanZero_thenCreateAccountAndTransaction() throws Exception {

    // When
    mvc.perform(
            post("/bank/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new AccountDTO(1, new BigDecimal("1.00")))))
            .andExpect(status().isOk());

    given(transactionIntegration
            .getAccountTransactions(accountRepository.findAll().iterator().next().getId()))
            .willReturn(List.of(new Transaction(1, CREDIT, new BigDecimal("1.00"))));

    var accounts = accountService.getAccounts(1);

    // Then
    assertThat(accounts.get(0).balance()).isEqualTo(new BigDecimal("1.00"));

    assertThat(accounts.get(0).type()).isEqualTo(CURRENT);

    assertThat(accounts.get(0).transactions()).isNotNull();

    assertThat(accounts.get(0).transactions().size()).isEqualTo(1);
  }
}
