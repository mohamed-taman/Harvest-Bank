package org.siriusxi.blueharvest.bank.ts.service;

import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.ts.persistence.TransactionRepository;
import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public List<Transaction> getTransactions(int accountId) {
        return transactionRepository.findByAccountId(accountId)
                   .stream()
                   .map(entity -> new Transaction(accountId, entity.getType()
                       , entity.getAmount()))
                   .collect(Collectors.toList());
    }

    public void createTransaction(TransactionEntity entity) {
        transactionRepository.save(entity);
    }
}
