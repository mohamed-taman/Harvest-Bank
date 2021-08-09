package org.siriusxi.blueharvest.bank.common.api.composite.trx;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Transaction(@JsonProperty("accountId") int accountId,
@JsonProperty("type") TransactionType type,
@JsonProperty("amount") BigDecimal amount){
                            }
