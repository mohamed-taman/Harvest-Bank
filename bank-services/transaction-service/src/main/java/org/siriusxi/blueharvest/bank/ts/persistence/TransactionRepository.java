package org.siriusxi.blueharvest.bank.ts.persistence;

import org.siriusxi.blueharvest.bank.ts.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findByAccountId(int customerId);
}
