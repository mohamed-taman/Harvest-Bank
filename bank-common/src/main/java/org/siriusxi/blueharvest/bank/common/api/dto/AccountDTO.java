package org.siriusxi.blueharvest.bank.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AccountDTO {

    private int customerId;
    @NonNull
    private BigDecimal initialCredit;

}
