package com.renansouza.rf;

import com.renansouza.base.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fixed_incomes"/*, uniqueConstraints =  @UniqueConstraint(columnNames={"user", "issuer"})*/)
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="FixedIncome", description="Fixed income description")
public class FixedIncome extends Auditable {

//    @Column(name = "issuer")
//    private Company issuer;

    private String description;

    private String index;
    private BigDecimal rate;

    private LocalDate date;
    private FixedIncomeType type;

//    @JsonIgnore
//    private List<Transaction> transactions;

//    @Column(name = "user")
//    private User user;


}