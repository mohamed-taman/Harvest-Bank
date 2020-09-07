package org.siriusxi.blueharvest.bank.cs.api;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.api.composite.customer.CustomerAggregate;
import org.siriusxi.blueharvest.bank.common.api.dto.AccountDTO;
import org.siriusxi.blueharvest.bank.common.exception.InvalidInputException;
import org.siriusxi.blueharvest.bank.cs.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("bank/api/v1")
@Log4j2
public class CustomerController {

  private final CustomerService customerService;

  @Autowired
  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping(
          value = "customers",
          produces = APPLICATION_JSON_VALUE)
  public List<CustomerAggregate> getCustomers() {
    return customerService.getCustomers();
  }

  @PostMapping(
          value = "customers/{id}/accounts",
          consumes = APPLICATION_JSON_VALUE)
  public void createAccount(@PathVariable int id, @RequestBody AccountDTO account) {

    log.debug("createAccount: creates a new account {}", account);

    requireNonNull(account, "Invalid account data.");

    if (id <= 0 || account.getInitialCredit().doubleValue() < 0.0)
      throw new InvalidInputException(format("Invalid account data %s", account));

    account.setCustomerId(id);
    customerService.createCustomerAccount(account);
  }
}
