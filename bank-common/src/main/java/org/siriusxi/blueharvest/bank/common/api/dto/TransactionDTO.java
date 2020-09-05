package org.siriusxi.blueharvest.bank.common.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {

  private int accountId;
  private BigDecimal amount;
}
