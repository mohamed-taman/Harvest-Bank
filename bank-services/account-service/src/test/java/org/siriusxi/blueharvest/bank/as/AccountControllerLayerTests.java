package org.siriusxi.blueharvest.bank.as;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.as.api.AccountController;
import org.siriusxi.blueharvest.bank.as.service.AccountService;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.siriusxi.blueharvest.bank.common.api.composite.account.AccountType.CURRENT;
import static org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType.CREDIT;
import static org.siriusxi.blueharvest.bank.util.JsonUtilities.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerLayerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    void setup() {
        // Given
        given(accountService.getAccounts(1))
            .willReturn(
                List.of(
                    new Account(
                        1,
                        new BigDecimal("1.0"),
                        CURRENT,
                        List.of(new Transaction(1, CREDIT, new BigDecimal("1.0"))))));
    }

    @Test
    void whenInitialCreditGreaterThanZero_thenCreateAccountAndTransaction() throws Exception {

        // When
        mvc.perform(
            post("/bank/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(1, new BigDecimal("1.0")))))
            .andExpect(status().isOk());

        var accounts = accountService.getAccounts(1);

        // Then
        assertThat(accounts.get(0).balance()).isEqualTo(new BigDecimal("1.0"));

        assertThat(accounts.get(0).type()).isEqualTo(CURRENT);

        assertThat(accounts.get(0).transactions()).isNotNull();
        assertThat(accounts.get(0).transactions().size()).isEqualTo(1);
    }

    @Test
    void getAccountsByCustomerId_thenReturnAccountsAndTransactionsJsonArray() throws Exception {

        // When
        mvc.perform(get("/bank/api/v1/accounts?customerId=1")
                        .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].balance", is(1.0)))
            .andExpect(jsonPath("$[0].customerId", is(1)))
            .andExpect(jsonPath("$[0].type", is("CURRENT")))
            .andExpect(jsonPath("$[0].transactions", hasSize(1)))
            .andExpect(jsonPath("$[0].transactions[0].accountId", is(1)))
            .andExpect(jsonPath("$[0].transactions[0].type", is("CREDIT")))
            .andExpect(jsonPath("$[0].transactions[0].amount", is(1.0)));
    }
}
