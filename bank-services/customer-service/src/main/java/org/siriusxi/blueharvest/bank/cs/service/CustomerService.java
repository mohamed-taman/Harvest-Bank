package org.siriusxi.blueharvest.bank.cs.service;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.customer.CustomerAggregate;
import org.siriusxi.blueharvest.bank.cs.integration.AccountIntegration;
import org.siriusxi.blueharvest.bank.cs.persistence.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log4j2
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountIntegration accountIntegration;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           AccountIntegration accountIntegration ) {
        this.customerRepository = customerRepository;
        this.accountIntegration = accountIntegration;
    }

    public List<CustomerAggregate> getCustomers(){

        return StreamSupport.
                stream(customerRepository.findAll().spliterator(), false)
                .map(entity -> new CustomerAggregate(
                        entity.getId(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getBalance(),
                        accountIntegration
                                .getCustomerAccounts(entity.getId())))
                .collect(Collectors.toList());
    }
}
