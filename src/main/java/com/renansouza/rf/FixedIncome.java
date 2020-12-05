package com.renansouza.rf;

import com.renansouza.config.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="FixedIncome", description="Fixed income description")
public class FixedIncome extends Auditable {

    @Id
    @GeneratedValue
    private Long id;

    private String issuer;

    private String index;
    private BigDecimal rate;

    private LocalDate date;
    private LocalDate dueDate;
    private FixedIncomeType type;

    private BigDecimal amount;

    @Transient
    private BigDecimal ir = BigDecimal.ZERO;
    @Transient
    private BigDecimal iof = BigDecimal.ZERO;
    @Transient
    private BigDecimal grossIncome = BigDecimal.ZERO;

}



