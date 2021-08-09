package org.siriusxi.blueharvest.bank.cs.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "customer")
@Data
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    private BigDecimal balance;

}
