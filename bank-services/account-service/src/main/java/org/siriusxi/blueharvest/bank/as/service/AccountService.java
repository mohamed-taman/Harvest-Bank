package org.siriusxi.blueharvest.bank.as.service;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.as.integration.TransactionIntegration;
import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.siriusxi.blueharvest.bank.as.persistence.AccountRepository;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.siriusxi.blueharvest.bank.common.api.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <code>AccountService</code> class is a service layer in Account Microservice, that hold all business logic
 * for Harvest
 * Bank Accounts.
 *
 * @author Mohamed Taman
 * @version 0.5
 * @since Harvest beta v0.1
 */
@Service
@Log4j2
public class AccountService {

  private final AccountRepository accountRepository;
  private final TransactionIntegration transactionIntegration;

  @Autowired
  public AccountService(
      AccountRepository accountRepository, TransactionIntegration transactionIntegration) {
    this.accountRepository = accountRepository;
    this.transactionIntegration = transactionIntegration;
  }

  /**
   * This method <code>getAccounts()<code/> return the accounts information with related
   * transactions.
   *
   * @param customerId customer id
   * @return List&lt;Account&gt; accounts and related transactions information.
   * @since Harvest beta v0.1
   */
  public List<Account> getAccounts(int customerId) {

    log.trace("Calling - getAccounts -> Getting all accounts for customer ID {}", customerId);

    var accounts =
        accountRepository.findByCustomerId(customerId).stream()
            .map(
                entity ->
                    new Account(
                        customerId,
                        entity.getBalance(),
                        entity.getType(),
                        transactionIntegration.getAccountTransactions(entity.getId())))
            .collect(Collectors.toList());

    log.trace("Returning - getAccounts -> With {} accounts", accounts.size());

    return accounts;
  }

  /**
   * This method <code>createAccount()</code> is responsible to create customer accounts with the
   * following logic:
   *
   * <ul>
   *   <li>Once the endpoint is called, a new account will be opened connected to the user whose ID
   *       is customerID.
   *   <li>Also, if initialCredit is not 0, a transaction will be sent to the new account.
   * </ul>
   *
   * @param entity is the account to save.
   * @since Harvest beta v0.1
   */
  public void createAccount(AccountEntity entity) {
    log.trace("Calling - createAccount -> to save account {}", entity);
    accountRepository.save(entity);

    if (entity.getBalance().doubleValue() > 0.0) {
      transactionIntegration.createTransaction(
          new TransactionDTO(entity.getId(), entity.getBalance()));

      log.debug("New account transaction is created for account {}", entity.getId());
    }

    log.trace("Returning - createAccount -> account {} saved successfully", entity);
  }
}
