package org.siriusxi.blueharvest.bank.cs.persistence;

import org.siriusxi.blueharvest.bank.cs.persistence.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Integer> {
}
