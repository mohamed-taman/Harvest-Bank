package org.siriusxi.blueharvest.bank.ts.api;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.siriusxi.blueharvest.bank.ts.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("bank/api/v1")
@Log4j2
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(
        value = "transactions",
        produces = APPLICATION_JSON_VALUE)
    public List<Transaction> getTransactions(@RequestParam("accountId") int accountId) {
        return transactionService.getTransactions(accountId);
    }

    @PostMapping(
        value = "/transactions",
        consumes = APPLICATION_JSON_VALUE)
    public void createTransaction(@RequestBody TransactionDTO transaction) {
        transactionService.createTransaction(new TransactionEntity(transaction.getAccountId(),
            transaction.getAmount()));
        log.debug("createTransaction: creates a new Transaction {}", transaction);
    }
}
