package org.siriusxi.blueharvest.bank.as.service;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.as.integration.TransactionIntegration;
import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.siriusxi.blueharvest.bank.as.persistence.AccountRepository;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.siriusxi.blueharvest.bank.common.api.composite.trx.Transaction;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log4j2
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionIntegration transactionIntegration;


    @Autowired
    public AccountService(AccountRepository accountRepository,
                          TransactionIntegration transactionIntegration) {
        this.accountRepository = accountRepository;
        this.transactionIntegration = transactionIntegration;
    }

    public List<Account> getAccounts(int customerId) {
        return StreamSupport
                .stream(accountRepository.findByCustomerId(customerId).spliterator(),
                false)
                .map(entity -> new Account(customerId,entity.getBalance(), entity.getType()
                ,transactionIntegration.getAccountTransactions(entity.getId())))
                .collect(Collectors.toList());
    }

    public void createAccount(AccountEntity entity) {
        accountRepository.save(entity);
        log.debug("Account created with ID -----> {}", entity.getId());
    }
}
