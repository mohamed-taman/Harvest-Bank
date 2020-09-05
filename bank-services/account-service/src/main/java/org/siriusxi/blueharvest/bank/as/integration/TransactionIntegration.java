package org.siriusxi.blueharvest.bank.as.integration;

import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionIntegration {

    public List<Transaction> getAccountTransactions(int id){
        return List.of();
    }

    public void createTransaction(TransactionDTO transaction) {

    }
}
