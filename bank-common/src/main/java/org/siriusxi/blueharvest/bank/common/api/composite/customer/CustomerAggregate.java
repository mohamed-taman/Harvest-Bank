package org.siriusxi.blueharvest.bank.common.api.composite.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.siriusxi.blueharvest.bank.common.api.composite.account.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * Record <code>CustomerAggregate</code> that hold all the Customer aggregate information.
 *
 * @implNote Since it is a record and not normal POJO, so it needs some customizations
 * to be serialized to JSON and this is done with method
 * <code>GlobalConfiguration.jacksonCustomizer()</code>.
 *
 * @see java.lang.Record
 * @author mohamed.taman
 * @version v0.1
 * @since v0.1
 */
public record CustomerAggregate(
        @JsonProperty("id") int id,
        @JsonProperty("name") String firstName,
        @JsonProperty("Surname")String lastName,
        @JsonProperty("balance") BigDecimal balance,
        @JsonProperty("accounts") List<Account> accounts) {
}
