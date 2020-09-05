package org.siriusxi.blueharvest.bank.ts.persistence;

import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Integer> {

    Iterable<TransactionEntity> findByAccountId(int customerId);
}
