package org.siriusxi.blueharvest.bank.cs.integration;

import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountIntegration {


    public List<Account> getCustomerAccounts(int id){
        return List.of();
    }

}
