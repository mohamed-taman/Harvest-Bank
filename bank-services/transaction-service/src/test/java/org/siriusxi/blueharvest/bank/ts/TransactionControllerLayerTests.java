package org.siriusxi.blueharvest.bank.ts;

import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.siriusxi.blueharvest.bank.ts.api.TransactionController;
import org.siriusxi.blueharvest.bank.ts.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;
import static org.siriusxi.blueharvest.bank.common.api.composite.trx.TransactionType.CREDIT;
import static org.siriusxi.blueharvest.bank.util.JsonUtilities.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerLayerTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private TransactionService transactionService;

  @Test
  void whenValidInput_thenCreateTransaction() throws Exception {
    //Given
    List<Transaction> allTransactions =
            List.of(
                    new Transaction(1, CREDIT, new BigDecimal("10000.77")));

    given(transactionService.getTransactions(1)).willReturn(allTransactions);

    //When
    mvc.perform(
        post("/bank/api/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(new TransactionDTO(1,new BigDecimal("10000.77")))));

    List<Transaction> found = transactionService.getTransactions(1);

    //Then
    assertThat(found.get(0).amount())
            .isEqualTo(new BigDecimal("10000.77"));

    assertThat(found.get(0).type())
            .isEqualTo(CREDIT);
  }

  @Test
  void givenTransactions_whenGetTransactions_thenReturnJsonArray() throws Exception {
    // Given
    List<Transaction> allTransactions =
        List.of(
            new Transaction(1, CREDIT, new BigDecimal("100.10")),
            new Transaction(1, TransactionType.DEBIT, new BigDecimal("90.11")));

    given(transactionService.getTransactions(1)).willReturn(allTransactions);

    //When
    mvc.perform(
            get("/bank/api/v1/transactions?accountId=1")
                    .contentType(MediaType.APPLICATION_JSON))
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
