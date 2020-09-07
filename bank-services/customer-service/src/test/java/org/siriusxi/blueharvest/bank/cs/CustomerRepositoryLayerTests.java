package org.siriusxi.blueharvest.bank.cs;

import org.junit.jupiter.api.Test;
import org.siriusxi.blueharvest.bank.cs.persistence.CustomerRepository;
import org.siriusxi.blueharvest.bank.cs.persistence.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryLayerTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void whenFindAllCustomers_thenReturn7Customers() {
        // given all 7 employees already initialized

        // when
        var found = customerRepository.findAll();

        // then
        assertThat(found.spliterator().getExactSizeIfKnown()).isEqualTo(7);
    }

    @Test
    void updateCustomerBalance() {
        // given 7th employee already initialized with balance 0.0
        var foundCustomer = customerRepository.findById(7);

        CustomerEntity customer = null;

        if (foundCustomer.isPresent())
            customer = foundCustomer.get();

        assertThat(customer.getBalance()).isEqualTo(new BigDecimal("0.00"));

        // when
        customer.setBalance(new BigDecimal("9999.90"));
        customerRepository.save(customer);

        // then
        customer = null;

        foundCustomer = customerRepository.findById(7);

        if (foundCustomer.isPresent())
            customer = foundCustomer.get();

        assertThat(customer.getBalance()).isEqualTo(new BigDecimal("9999.90"));
    }

}
