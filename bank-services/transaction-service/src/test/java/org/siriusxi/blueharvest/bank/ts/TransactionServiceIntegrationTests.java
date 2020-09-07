package org.siriusxi.blueharvest.bank.ts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.siriusxi.blueharvest.bank.ts.persistence.TransactionRepository;
import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.siriusxi.blueharvest.bank.ts.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType.CREDIT;
import static org.siriusxi.blueharvest.bank.util.JsonUtilities.toJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = TransactionServiceApplication.class)
@AutoConfigureMockMvc
class TransactionServiceIntegrationTests {

  @Autowired private MockMvc mvc;

  @Autowired private TransactionService transactionService;

  @Autowired private TransactionRepository transactionRepository;

  @AfterEach
  public void resetDb() {
    transactionRepository.deleteAll();
  }

  @Test
  void whenValidInput_thenCreateTransaction() throws Exception {
    // Given transaction
    mvc.perform(
            post("/bank/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new TransactionDTO(1, new BigDecimal("10000.77")))))
        .andExpect(status().isOk());

    // When
    List<Transaction> found = transactionService.getTransactions(1);

    // Then
    assertThat(found.get(0).amount()).isEqualTo(new BigDecimal("10000.77"));
    assertThat(found.get(0).type()).isEqualTo(CREDIT);
  }

  @Test
  void givenTransactions_whenGetTransactions_thenReturnJsonArray() throws Exception {
    // Given
    TransactionEntity trx = new TransactionEntity(1, new BigDecimal("90.11"));
    trx.setType(TransactionType.DEBIT);

    List.of(new TransactionEntity(1, new BigDecimal("100.10")), trx)
        .forEach(entity -> transactionService.createTransaction(entity));

    // When
    mvc.perform(
            get("/bank/api/v1/transactions?accountId=1").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        // Then
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].amount", is(100.10)))
        .andExpect(jsonPath("$[0].accountId", is(1)))
        .andExpect(jsonPath("$[0].type", is("CREDIT")))
        // And second transaction
        .andExpect(jsonPath("$[1].amount", is(90.11)))
        .andExpect(jsonPath("$[1].accountId", is(1)))
        .andExpect(jsonPath("$[1].type", is("DEBIT")));
  }
}
