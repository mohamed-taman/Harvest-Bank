package org.siriusxi.blueharvest.bank.as.persistence;

import org.siriusxi.blueharvest.bank.as.persistence.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, Integer> {

    List<AccountEntity> findByCustomerId(int customerId);
}
