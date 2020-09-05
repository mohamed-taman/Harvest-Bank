package org.siriusxi.blueharvest.bank.common.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {

    private int customerId;
    private BigDecimal initialCredit;

}
