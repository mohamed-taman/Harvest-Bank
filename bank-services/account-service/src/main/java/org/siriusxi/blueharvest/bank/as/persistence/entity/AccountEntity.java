package org.siriusxi.blueharvest.bank.as.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.siriusxi.blueharvest.bank.common.api.composite.account.AccountType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AccountEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;

  @NonNull
  private int customerId;
  private AccountType type = AccountType.CURRENT;

  @NonNull
  private BigDecimal balance;
}
