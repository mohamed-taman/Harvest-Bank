package org.siriusxi.blueharvest.bank.as.persistence;

import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, Integer> {

    Iterable<AccountEntity> findByCustomerId(int customerId);
}
