package org.siriusxi.blueharvest.bank.cs.service;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.customer.CustomerAggregate;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.siriusxi.blueharvest.bank.common.exception.NotFoundException;
import org.siriusxi.blueharvest.bank.cs.integration.AccountIntegration;
import org.siriusxi.blueharvest.bank.cs.persistence.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

/**
 * <code>CustomerService</code> class is a service layer in Harvest Microservice, that hold all business logic
 * for Harvest Bank customers.
 *
 * @author Mohamed Taman
 * @version 0.5
 * @since Harvest beta v0.1
 */
@Service
@Log4j2
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountIntegration accountIntegration;

    @Autowired
    public CustomerService(
        CustomerRepository customerRepository, AccountIntegration accountIntegration) {
        this.customerRepository = customerRepository;
        this.accountIntegration = accountIntegration;
    }

    /**
     * This method <code>getCustomers()<code/> return the user information showing Name, Surname,
     * balance, and transactions of the accounts.
     *
     * @return List&lt;CustomerAggregate&gt; customers information.
     * @since Harvest beta v0.1
     */
    public List<CustomerAggregate> getCustomers() {

        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                   .map(
                       entity ->
                           new CustomerAggregate(
                               entity.getId(),
                               entity.getFirstName(),
                               entity.getLastName(),
                               entity.getBalance(),
                               accountIntegration.getCustomerAccounts(entity.getId())))
                   .collect(Collectors.toList());
    }

    /**
     * This method accepts <strong>customer Id</strong> and <strong>initial credit</strong> and then
     * create the customer account and related transaction if any.
     *
     * @param account info to be created
     * @since Harvest beta v0.1
     */
    public void createCustomerAccount(AccountDTO account) {
        // get User first
        var customer =
            customerRepository
                .findById(account.getCustomerId())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            format("No Customer found for id {%d}", account.getCustomerId())));
        // create customer account
        accountIntegration.createAccount(account);

        // Update customer balance
        customer.setBalance(customer.getBalance().add(account.getInitialCredit()));

        // Update customer
        customerRepository.save(customer);
    }
}
