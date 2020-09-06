package org.siriusxi.blueharvest.bank.common.api.composite.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;

import java.math.BigDecimal;
import java.util.List;

public record Account(@JsonProperty("customerId") int customerId,
                      @JsonProperty("balance") BigDecimal balance,
                      @JsonProperty("type") AccountType type,
                      @JsonProperty("transactions") List<Transaction> transactions) {
}
