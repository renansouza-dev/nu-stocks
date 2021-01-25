package com.renansouza.base;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@MappedEntity(value = "fixed_incomes_transaction")
@Introspected
@EqualsAndHashCode(callSuper = true)
public class Transaction extends Auditable {

//    @Column(name = "fixed_incomes")
//    private FixedIncome fixedIncome;

    private LocalDate date = LocalDate.now();
    private LocalDate dueDate = LocalDate.now();

    private BigDecimal amount = BigDecimal.ZERO;

}