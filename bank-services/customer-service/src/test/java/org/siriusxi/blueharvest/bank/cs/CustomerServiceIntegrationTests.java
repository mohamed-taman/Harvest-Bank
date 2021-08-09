package org.siriusxi.blueharvest.bank.cs;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.siriusxi.blueharvest.bank.cs.integration.AccountIntegration;
import org.siriusxi.blueharvest.bank.cs.persistence.CustomerRepository;
import org.siriusxi.blueharvest.bank.cs.persistence.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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
    classes = CustomerServiceApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountIntegration accountIntegration;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Order(1)
    void createAccountForCustomerDoesNotExist() throws Exception {

        // When
        mvc.perform(
            post("/bank/api/v1/customers/12/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("1.00")))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is("NOT_FOUND")))
            .andExpect(jsonPath("$.message", is("No Customer found for id {12}")));
    }

    @Test
    @Order(2)
    void createAccountInvalidCustomerIdLessThanZero() throws Exception {

        // When
        mvc.perform(
            post("/bank/api/v1/customers/-1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("100.00")))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
            .andExpect(
                jsonPath(
                    "$.message",
                    is("Invalid data (Customer Id= -1, initialCredit= 100.00)")));
    }

    @Test
    @Order(3)
    void createAccountInvalidCreditLessThanZero() throws Exception {

        // When
        mvc.perform(
            post("/bank/api/v1/customers/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("-100.00")))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status", is("UNPROCESSABLE_ENTITY")))
            .andExpect(
                jsonPath(
                    "$.message",
                    is("Invalid data (Customer Id= 1, initialCredit= -100.00)")));
    }

    @Test
    @Order(4)
    void createAccountWithCreditZero_ThenGetCustomerWitAccountNoTransaction() throws Exception {

        // Given
        mvc.perform(
            post("/bank/api/v1/customers/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("0.00")))))
            .andExpect(status().isOk());

        given(accountIntegration.getCustomerAccounts(1))
            .willReturn(
                List.of(new Account(1,
                    new BigDecimal("0.0"), CURRENT, Collections.emptyList())));

        // When
        mvc.perform(get("/bank/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[0].accounts", hasSize(1)))
            .andExpect(jsonPath("$[0].balance", is(0.0)))
            .andExpect(jsonPath("$[0].accounts[0].transactions", hasSize(0)));

    }

    @Test
    @Order(5)
    void createAccountWithCredit_ThenGetCustomerWitAccountAndTransaction() throws Exception {

        // Given
        mvc.perform(
            post("/bank/api/v1/customers/2/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("100.20")))))
            .andExpect(status().isOk());

        given(accountIntegration.getCustomerAccounts(2))
            .willReturn(
                List.of(new Account(2,
                    new BigDecimal("100.20"), CURRENT,
                    List.of(new Transaction(1, CREDIT, new BigDecimal("100.20"))))));

        // When
        mvc.perform(get("/bank/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[1].accounts", hasSize(1)))
            //Customer Balance
            .andExpect(jsonPath("$[1].balance", is(100.20)))
            .andExpect(jsonPath("$[1].accounts[0].balance", is(100.20)))
            .andExpect(jsonPath("$[1].accounts[0].transactions", hasSize(1)))
            .andExpect(jsonPath("$[1].accounts[0].transactions[0].amount", is(100.20)));
    }

    @Test
    @Order(6)
    void create2AccountsForTheSameCustomer_thenCustomerBalanceGetUpdated() throws Exception {

        // Given
        mvc.perform(
            post("/bank/api/v1/customers/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("100.20")))))
            .andExpect(status().isOk());

        mvc.perform(
            post("/bank/api/v1/customers/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AccountDTO(new BigDecimal("100.20")))))
            .andExpect(status().isOk());

        given(accountIntegration.getCustomerAccounts(1))
            .willReturn(
                List.of(new Account(1,
                    new BigDecimal("100.20"), CURRENT,
                    List.of(new Transaction(1, CREDIT, new BigDecimal("100.20")))
                ), new Account(1,
                    new BigDecimal("100.20"), CURRENT,
                    List.of(new Transaction(1, CREDIT, new BigDecimal("100.20"))))));

        // When
        mvc.perform(get("/bank/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[0].accounts", hasSize(2)))
            //Customer Balance is updated
            .andExpect(jsonPath("$[0].balance", is(200.40)))
            .andExpect(jsonPath("$[0].accounts[0].balance", is(100.20)))
            .andExpect(jsonPath("$[0].accounts[0].transactions", hasSize(1)))
            .andExpect(jsonPath("$[0].accounts[0].transactions[0].amount", is(100.20)));

        var found = customerRepository.findById(1);
        CustomerEntity customer = null;
        if (found.isPresent())
            customer = found.get();

        assert customer != null;
        assertThat(customer.getBalance()).isEqualTo(new BigDecimal("200.40"));
    }

    @Test
    @Order(7)
    void getAllSevenCustomers() throws Exception {

        mvc.perform(get("/bank/api/v1/customers").contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(7)));
    }
}
