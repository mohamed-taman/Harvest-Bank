package org.siriusxi.blueharvest.bank.common.api.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class TransactionDTO {

    @NonNull
    private int accountId;

    @NonNull
    private BigDecimal amount;
}
